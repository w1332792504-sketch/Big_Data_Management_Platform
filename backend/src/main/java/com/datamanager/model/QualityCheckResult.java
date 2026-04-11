package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据质量检查结果实体
 */
@Data
@Entity
@Table(name = "quality_check_result")
public class QualityCheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ruleId;

    @Column(length = 100)
    private String ruleName;

    @Column(nullable = false)
    private Long datasourceId;

    @Column(length = 100)
    private String tableName;

    @Column(length = 100)
    private String columnName;

    @Column(length = 20)
    private String checkStatus; // PASS, FAIL, WARNING

    private Long totalRecords;

    private Long passRecords;

    private Long failRecords;

    private Double passRate;

    @Column(columnDefinition = "TEXT")
    private String checkDetail;

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    private LocalDateTime checkTime;

    private Long duration; // 检查耗时(毫秒)
}
