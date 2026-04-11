package com.datamanager.service;

import com.datamanager.dto.ApiResponse;
import com.datamanager.exception.NotFoundException;
import com.datamanager.model.DataSource;
import com.datamanager.model.QualityCheckResult;
import com.datamanager.model.QualityRule;
import com.datamanager.repository.DataSourceRepository;
import com.datamanager.repository.QualityCheckResultRepository;
import com.datamanager.repository.QualityRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据质量服务
 */
@Service
public class QualityService {

    private static final Logger log = LoggerFactory.getLogger(QualityService.class);

    @Autowired
    private QualityRuleRepository qualityRuleRepository;

    @Autowired
    private QualityCheckResultRepository qualityCheckResultRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    // 规则管理
    public List<QualityRule> findAllRules() {
        return qualityRuleRepository.findAll();
    }

    public List<QualityRule> findRulesByDatasource(Long datasourceId) {
        return qualityRuleRepository.findByDatasourceIdOrderByCreateTimeDesc(datasourceId);
    }

    public QualityRule findRuleById(Long id) {
        return qualityRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("规则不存在：" + id));
    }

    @Transactional
    public QualityRule saveRule(QualityRule rule) {
        return qualityRuleRepository.save(rule);
    }

    @Transactional
    public void deleteRule(Long id) {
        qualityRuleRepository.deleteById(id);
    }

    @Transactional
    public QualityRule updateRuleStatus(Long id, Integer status) {
        QualityRule rule = findRuleById(id);
        rule.setStatus(status);
        return qualityRuleRepository.save(rule);
    }

    // 执行质量检查
    @Transactional
    public QualityCheckResult executeCheck(Long ruleId) {
        QualityRule rule = findRuleById(ruleId);
        DataSource dataSource = dataSourceRepository.findById(rule.getDatasourceId())
                .orElseThrow(() -> new NotFoundException("数据源不存在"));

        QualityCheckResult result = new QualityCheckResult();
        result.setRuleId(ruleId);
        result.setRuleName(rule.getRuleName());
        result.setDatasourceId(rule.getDatasourceId());
        result.setTableName(rule.getTableName());
        result.setColumnName(rule.getColumnName());
        result.setCheckTime(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        try {
            // 执行检查SQL
            CheckResult checkResult = executeQualitySQL(dataSource, rule);

            result.setTotalRecords(checkResult.totalRecords);
            result.setPassRecords(checkResult.passRecords);
            result.setFailRecords(checkResult.failRecords);
            result.setPassRate(checkResult.passRate);
            result.setCheckDetail(checkResult.detail);

            // 根据通过率判断状态
            if (checkResult.passRate >= 95.0) {
                result.setCheckStatus("PASS");
            } else if (checkResult.passRate >= 80.0) {
                result.setCheckStatus("WARNING");
            } else {
                result.setCheckStatus("FAIL");
            }

        } catch (Exception e) {
            log.error("执行质量检查失败：{}", rule.getRuleName(), e);
            result.setCheckStatus("FAIL");
            result.setErrorMsg(e.getMessage());
        }

        result.setDuration(System.currentTimeMillis() - startTime);
        return qualityCheckResultRepository.save(result);
    }

    private CheckResult executeQualitySQL(DataSource dataSource, QualityRule rule) throws Exception {
        String sql = buildCheckSQL(rule);
        CheckResult result = new CheckResult();

        try (Connection conn = DriverManager.getConnection(
                dataSource.getJdbcUrl(),
                dataSource.getUsername(),
                dataSource.getPassword())) {

            // 执行总数统计
            String countSQL = String.format("SELECT COUNT(*) FROM %s", rule.getTableName());
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(countSQL)) {
                if (rs.next()) {
                    result.totalRecords = rs.getLong(1);
                }
            }

            // 执行规则检查
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    result.failRecords = rs.getLong(1);
                }
            }

            result.passRecords = result.totalRecords - result.failRecords;
            result.passRate = result.totalRecords > 0
                    ? (result.passRecords * 100.0 / result.totalRecords)
                    : 100.0;
            result.detail = String.format("总数: %d, 通过: %d, 失败: %d, 通过率: %.2f%%",
                    result.totalRecords, result.passRecords, result.failRecords, result.passRate);

        }

        return result;
    }

    private String buildCheckSQL(QualityRule rule) {
        String ruleType = rule.getRuleType();
        String tableName = rule.getTableName();
        String columnName = rule.getColumnName();

        switch (ruleType) {
            case "COMPLETENESS":
                // 完整性检查：检查空值
                return String.format("SELECT COUNT(*) FROM %s WHERE %s IS NULL OR %s = ''",
                        tableName, columnName, columnName);
            case "UNIQUENESS":
                // 唯一性检查：检查重复值
                return String.format(
                        "SELECT COUNT(*) FROM (SELECT %s FROM %s GROUP BY %s HAVING COUNT(*) > 1) t",
                        columnName, tableName, columnName);
            case "ACCURACY":
                // 准确性检查：使用自定义表达式
                if (rule.getRuleExpression() != null && !rule.getRuleExpression().isEmpty()) {
                    return String.format("SELECT COUNT(*) FROM %s WHERE NOT (%s)",
                            tableName, rule.getRuleExpression());
                }
                return String.format("SELECT COUNT(*) FROM %s WHERE %s IS NULL", tableName, columnName);
            case "CONSISTENCY":
                // 一致性检查
                return rule.getRuleExpression() != null
                        ? rule.getRuleExpression()
                        : String.format("SELECT COUNT(*) FROM %s WHERE %s IS NULL", tableName, columnName);
            case "TIMELINESS":
                // 时效性检查
                return rule.getRuleExpression() != null
                        ? rule.getRuleExpression()
                        : String.format("SELECT COUNT(*) FROM %s WHERE %s < DATE_SUB(NOW(), INTERVAL 30 DAY)",
                                tableName, columnName);
            default:
                return String.format("SELECT COUNT(*) FROM %s WHERE %s IS NULL", tableName, columnName);
        }
    }

    // 检查结果查询
    public Page<QualityCheckResult> findResults(Long datasourceId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return qualityCheckResultRepository.findByDatasourceIdOrderByCheckTimeDesc(datasourceId, pageable);
    }

    public List<QualityCheckResult> findResultsByRule(Long ruleId) {
        return qualityCheckResultRepository.findByRuleIdOrderByCheckTimeDesc(ruleId);
    }

    // 统计
    public Long countFailChecks() {
        return qualityCheckResultRepository.countByCheckStatus("FAIL");
    }

    // 批量执行检查
    @Transactional
    public void executeAllActiveRules() {
        List<QualityRule> rules = qualityRuleRepository.findByStatusOrderByCreateTimeDesc(1);
        for (QualityRule rule : rules) {
            try {
                executeCheck(rule.getId());
            } catch (Exception e) {
                log.error("执行规则检查失败：{}", rule.getRuleName(), e);
            }
        }
    }

    private static class CheckResult {
        long totalRecords;
        long passRecords;
        long failRecords;
        double passRate;
        String detail;
    }
}
