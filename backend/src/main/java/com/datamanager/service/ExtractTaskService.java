package com.datamanager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.datamanager.dto.ExtractTaskDTO;
import com.datamanager.exception.NotFoundException;
import com.datamanager.model.DataSource;
import com.datamanager.model.ExtractTask;
import com.datamanager.model.TaskExecution;
import com.datamanager.repository.DataSourceRepository;
import com.datamanager.repository.ExtractTaskRepository;
import com.datamanager.repository.TaskExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ExtractTaskService {

    private static final Logger log = LoggerFactory.getLogger(ExtractTaskService.class);

    @Autowired
    private ExtractTaskRepository extractTaskRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private TaskExecutionRepository taskExecutionRepository;

    @Value("${datax.home:/opt/datax}")
    private String dataxHome;

    @Value("${datax.job-path:/opt/datax/job}")
    private String jobPath;

    @Value("${datax.python-path:/opt/datax/bin/datax.py}")
    private String dataxPythonPath;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<Long, Process> runningTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 确保 job 目录存在
        new File(jobPath).mkdirs();
        // 恢复未完成的任务状态
        recoverTaskStatus();
    }

    private void recoverTaskStatus() {
        // 重启后将运行中的任务状态重置
        List<ExtractTask> runningTasks = extractTaskRepository.findByStatusOrderByCreateTimeDesc(1);
        for (ExtractTask task : runningTasks) {
            task.setStatus(0);
            extractTaskRepository.save(task);
            log.info("恢复任务状态：{} -> 已停止", task.getTaskName());
        }
    }

    public List<ExtractTaskDTO> findAll() {
        return extractTaskRepository.findAllByOrderByCreateTimeDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ExtractTaskDTO findById(Long id) {
        ExtractTask task = extractTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("任务不存在：" + id));
        ExtractTaskDTO dto = toDTO(task);

        // 填充数据源名称
        if (task.getSourceDatasourceId() != null) {
            dataSourceRepository.findById(task.getSourceDatasourceId())
                    .ifPresent(ds -> dto.setSourceDatasourceName(ds.getName()));
        }
        if (task.getTargetDatasourceId() != null) {
            dataSourceRepository.findById(task.getTargetDatasourceId())
                    .ifPresent(ds -> dto.setTargetDatasourceName(ds.getName()));
        }
        return dto;
    }

    @Transactional
    public ExtractTask save(ExtractTaskDTO dto) {
        ExtractTask entity = new ExtractTask();
        BeanUtils.copyProperties(dto, entity);
        return extractTaskRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        stopTask(id);
        extractTaskRepository.deleteById(id);
    }

    public Long runTask(Long taskId, String executionType, String operator) {
        ExtractTask task = extractTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在：" + taskId));

        // 检查是否正在运行
        if (task.getStatus() == 1) {
            throw new RuntimeException("任务正在运行中");
        }

        // 创建执行记录
        TaskExecution execution = new TaskExecution();
        execution.setTaskId(taskId);
        execution.setTaskName(task.getTaskName());
        execution.setExecutionType(executionType);
        execution.setStartTime(LocalDateTime.now());
        execution.setStatus("RUNNING");
        execution.setOperator(operator);
        taskExecutionRepository.save(execution);

        // 更新任务状态
        task.setStatus(1);
        task.setLastRunTime(LocalDateTime.now());
        extractTaskRepository.save(task);

        // 异步执行 DataX
        executor.submit(() -> executeDataX(task, execution));

        return execution.getId();
    }

    private void executeDataX(ExtractTask task, TaskExecution execution) {
        Process process = null;
        try {
            // 生成 DataX JSON 配置
            String jsonConfig = generateDataXJson(task);
            String jobFile = jobPath + "/" + task.getTaskName() + "_" + System.currentTimeMillis() + ".json";
            Files.writeString(Paths.get(jobFile), jsonConfig);

            // 构建命令
            List<String> command = List.of(
                    "python", dataxPythonPath,
                    jobFile
            );

            log.info("执行 DataX: {}", String.join(" ", command));

            // 启动进程
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(dataxHome));
            pb.redirectErrorStream(true);
            process = pb.start();

            // 保存进程引用
            runningTasks.put(task.getId(), process);

            // 读取日志
            StringBuilder logOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logOutput.append(line).append("\n");
                    log.info("[DataX] {}", line);
                }
            }

            // 等待完成
            int exitCode = process.waitFor();
            long duration = System.currentTimeMillis() - execution.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;

            // 更新执行记录
            execution.setEndTime(LocalDateTime.now());
            execution.setDuration(duration);
            execution.setLogPath(jobFile.replace(".json", ".log"));

            if (exitCode == 0) {
                execution.setStatus("SUCCESS");
                task.setLastRunStatus(0);
                // 尝试解析抽取记录数
                Long records = parseRecords(logOutput.toString());
                execution.setExtractCount(records);
                task.setLastRunRecords(records);
            } else {
                execution.setStatus("FAILED");
                execution.setErrorMsg(logOutput.toString());
                task.setLastRunStatus(1);
            }

            task.setLastRunDuration(duration);
            task.setStatus(2); // 执行完成
            runningTasks.remove(task.getId());

        } catch (Exception e) {
            log.error("执行任务失败：{}", task.getTaskName(), e);
            execution.setEndTime(LocalDateTime.now());
            execution.setStatus("FAILED");
            execution.setErrorMsg(e.getMessage());
            task.setStatus(3); // 失败
            task.setLastRunStatus(1);
            runningTasks.remove(task.getId());
        } finally {
            taskExecutionRepository.save(execution);
            extractTaskRepository.save(task);
        }
    }

    private String generateDataXJson(ExtractTask task) {
        DataSource source = dataSourceRepository.findById(task.getSourceDatasourceId())
                .orElseThrow(() -> new RuntimeException("源数据源不存在"));
        DataSource target = dataSourceRepository.findById(task.getTargetDatasourceId())
                .orElseThrow(() -> new RuntimeException("目标数据源不存在"));

        JSONObject job = new JSONObject();
        JSONObject content = new JSONObject();

        // Reader 配置
        JSONObject reader = buildReader(task, source);

        // Writer 配置
        JSONObject writer = buildWriter(task, target);

        content.put("reader", reader);
        content.put("writer", writer);

        JSONArray contentArray = new JSONArray();
        contentArray.add(content);
        job.put("content", contentArray);

        JSONObject setting = new JSONObject();
        JSONObject speed = new JSONObject();
        speed.put("channel", task.getSplitPkNum() != null ? task.getSplitPkNum() : 1);
        setting.put("speed", speed);
        job.put("setting", setting);

        JSONObject root = new JSONObject();
        root.put("job", job);

        return JSON.toJSONString(root, true);
    }

    private JSONObject buildReader(ExtractTask task, DataSource source) {
        JSONObject reader = new JSONObject();
        reader.put("name", getReaderName(source.getType()));

        JSONObject parameter = new JSONObject();
        parameter.put("username", source.getUsername());
        parameter.put("password", source.getPassword());

        JSONArray jdbcUrlArray = new JSONArray();
        jdbcUrlArray.add(source.getJdbcUrl());

        JSONArray connectionArray = new JSONArray();
        JSONObject connectionObj = new JSONObject();
        connectionObj.put("jdbcUrl", jdbcUrlArray);
        connectionArray.add(connectionObj);
        parameter.put("connection", connectionArray);

        if (task.getQuerySql() != null && !task.getQuerySql().isEmpty()) {
            JSONArray sqlArray = new JSONArray();
            sqlArray.add(task.getQuerySql());
            parameter.put("querySql", sqlArray);
        } else {
            parameter.put("table", task.getSourceTable());
            if (task.getFilterCondition() != null && !task.getFilterCondition().isEmpty()) {
                parameter.put("where", task.getFilterCondition());
            }
        }

        // 分片配置
        if (task.getSplitPk() != null && !task.getSplitPk().isEmpty() && task.getSplitPkNum() > 1) {
            parameter.put("splitPk", task.getSplitPk());
        }

        reader.put("parameter", parameter);
        return reader;
    }

    private JSONObject buildWriter(ExtractTask task, DataSource target) {
        JSONObject writer = new JSONObject();
        writer.put("name", getWriterName(target.getType()));

        JSONObject parameter = new JSONObject();
        parameter.put("username", target.getUsername());
        parameter.put("password", target.getPassword());
        JSONArray connectionArray = new JSONArray();
        JSONObject connectionObj = new JSONObject();
        connectionObj.put("jdbcUrl", target.getJdbcUrl());
        connectionObj.put("table", task.getTargetTable());
        connectionArray.add(connectionObj);
        parameter.put("connection", connectionArray);
        parameter.put("writeMode", task.getWriteMode() != null ? task.getWriteMode() : "insert");
        parameter.put("preSql", new JSONArray());
        parameter.put("postSql", new JSONArray());

        writer.put("parameter", parameter);
        return writer;
    }

    private String getReaderName(String type) {
        switch (type.toUpperCase()) {
            case "MYSQL": return "mysqlreader";
            case "HIVE": return "hdfsreader";
            case "ORACLE": return "oraclereader";
            case "POSTGRESQL": return "postgresqlreader";
            default: return "mysqlreader";
        }
    }

    private String getWriterName(String type) {
        switch (type.toUpperCase()) {
            case "MYSQL": return "mysqlwriter";
            case "HIVE": return "hfswriter";
            case "ORACLE": return "oraclewriter";
            case "POSTGRESQL": return "postgresqlwriter";
            default: return "mysqlwriter";
        }
    }

    private Long parseRecords(String logOutput) {
        try {
            // 从 DataX 日志中解析记录数
            if (logOutput.contains("Total")) {
                String[] parts = logOutput.split("Total");
                if (parts.length > 1) {
                    String numStr = parts[1].replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        return Long.parseLong(numStr);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析记录数失败：{}", e.getMessage());
        }
        return 0L;
    }

    @Transactional
    public void stopTask(Long taskId) {
        Process process = runningTasks.get(taskId);
        if (process != null) {
            process.destroyForcibly();
            runningTasks.remove(taskId);
        }

        ExtractTask task = extractTaskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setStatus(2);
            extractTaskRepository.save(task);
        }
    }

    public Page<TaskExecution> findExecutions(Long taskId, Pageable pageable) {
        return taskExecutionRepository.findByTaskIdOrderByStartTimeDesc(taskId, pageable);
    }

    public TaskExecution findLatestExecution(Long taskId) {
        List<TaskExecution> executions = taskExecutionRepository.findByTaskIdAndStatusOrderByStartTimeDesc(taskId, "SUCCESS");
        return executions.isEmpty() ? null : executions.get(0);
    }

    private ExtractTaskDTO toDTO(ExtractTask entity) {
        ExtractTaskDTO dto = new ExtractTaskDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getLastRunTime() != null) {
            dto.setLastRunTime(entity.getLastRunTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli());
        }
        return dto;
    }
}
