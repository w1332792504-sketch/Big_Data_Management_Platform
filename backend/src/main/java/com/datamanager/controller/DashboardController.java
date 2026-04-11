package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.DataSource;
import com.datamanager.model.ExtractTask;
import com.datamanager.model.TaskExecution;
import com.datamanager.repository.DataSourceRepository;
import com.datamanager.repository.ExtractTaskRepository;
import com.datamanager.repository.TaskExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private ExtractTaskRepository extractTaskRepository;

    @Autowired
    private TaskExecutionRepository taskExecutionRepository;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 数据源统计
        long datasourceCount = dataSourceRepository.count();
        stats.put("datasourceCount", datasourceCount);

        // 任务统计
        long taskCount = extractTaskRepository.count();
        stats.put("taskCount", taskCount);

        // 执行记录统计
        long successCount = taskExecutionRepository.countByStatus("COMPLETED");
        long failedCount = taskExecutionRepository.countByStatus("FAILED");
        stats.put("successCount", successCount);
        stats.put("failedCount", failedCount);

        // 数据源类型分布
        List<DataSource> dataSources = dataSourceRepository.findAll();
        Map<String, Long> datasourceTypeMap = dataSources.stream()
                .collect(Collectors.groupingBy(DataSource::getType, Collectors.counting()));

        List<Map<String, Object>> datasourceStats = new ArrayList<>();
        datasourceTypeMap.forEach((type, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", type);
            item.put("count", count);
            item.put("percentage", (int) (count * 100.0 / datasourceCount));
            datasourceStats.add(item);
        });
        stats.put("datasourceStats", datasourceStats);

        // 任务状态分布
        List<ExtractTask> tasks = extractTaskRepository.findAll();
        Map<Integer, Long> taskStatusMap = tasks.stream()
                .collect(Collectors.groupingBy(ExtractTask::getStatus, Collectors.counting()));

        List<Map<String, Object>> taskStats = new ArrayList<>();
        taskStatusMap.forEach((status, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("status", status);
            item.put("statusText", getStatusText(status));
            item.put("count", count);
            item.put("percentage", (int) (count * 100.0 / taskCount));
            taskStats.add(item);
        });
        stats.put("taskStats", taskStats);

        return ApiResponse.success(stats);
    }

    @GetMapping("/recent-executions")
    public ApiResponse<List<Map<String, Object>>> getRecentExecutions() {
        List<TaskExecution> executions = taskExecutionRepository.findTop10ByOrderByStartTimeDesc();

        List<Map<String, Object>> result = new ArrayList<>();
        for (TaskExecution execution : executions) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", execution.getId());
            item.put("taskName", execution.getTaskName());
            item.put("executionType", execution.getExecutionType());
            item.put("status", execution.getStatus());
            item.put("startTime", execution.getStartTime());
            item.put("duration", execution.getDuration());
            result.add(item);
        }

        return ApiResponse.success(result);
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0: return "已停止";
            case 1: return "运行中";
            case 2: return "已完成";
            case 3: return "失败";
            default: return "未知";
        }
    }
}
