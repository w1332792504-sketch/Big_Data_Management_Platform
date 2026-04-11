package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 告警规则实体
 */
@Data
@Entity
@Table(name = "alert_rule")
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Column(length = 50)
    private String alertType; // TASK_FAIL, TASK_TIMEOUT, QUALITY_FAIL, SYSTEM

    @Column(length = 50)
    private String alertLevel; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String alertCondition;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String notifyChannel; // EMAIL, SMS, WEBHOOK

    @Column(columnDefinition = "TEXT")
    private String notifyConfig;

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
