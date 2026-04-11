package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String module;

    @Column(length = 50)
    private String operation;

    @Column(length = 200)
    private String description;

    @Column(length = 100)
    private String operator;

    private Long operatorId;

    @Column(length = 50)
    private String ip;

    @Column(length = 50)
    private String method;

    @Column(length = 500)
    private String requestUrl;

    @Column(columnDefinition = "TEXT")
    private String requestParams;

    @Column(columnDefinition = "TEXT")
    private String responseData;

    private Integer responseStatus;

    private Long duration;

    @Column(length = 20)
    private String status; // SUCCESS, FAIL

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
