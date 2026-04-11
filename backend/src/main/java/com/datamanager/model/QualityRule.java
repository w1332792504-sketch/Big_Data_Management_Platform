package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据质量规则实体
 */
@Data
@Entity
@Table(name = "quality_rule")
public class QualityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Column(length = 50)
    private String ruleType; // COMPLETENESS, ACCURACY, CONSISTENCY, TIMELINESS, UNIQUENESS

    @Column(nullable = false)
    private Long datasourceId;

    @Column(length = 100)
    private String tableName;

    @Column(length = 100)
    private String columnName;

    @Column(columnDefinition = "TEXT")
    private String ruleExpression;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String severity; // HIGH, MEDIUM, LOW

    private Integer status; // 0-禁用, 1-启用

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
