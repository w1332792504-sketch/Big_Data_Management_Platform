package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 告警记录实体
 */
@Data
@Entity
@Table(name = "alert_record")
public class AlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ruleId;

    @Column(length = 100)
    private String ruleName;

    @Column(length = 50)
    private String alertType;

    @Column(length = 50)
    private String alertLevel;

    @Column(columnDefinition = "TEXT")
    private String alertContent;

    @Column(length = 50)
    private String status; // PENDING, SENT, ACKNOWLEDGED, RESOLVED

    @Column(length = 100)
    private String notifyChannel;

    private LocalDateTime alertTime;

    private LocalDateTime notifyTime;

    private LocalDateTime acknowledgeTime;

    private String acknowledgedBy;

    private LocalDateTime resolveTime;

    private String resolvedBy;

    @Column(columnDefinition = "TEXT")
    private String remark;
}
