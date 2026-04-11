package com.datamanager.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据抽取任务配置表
 */
@Data
@Entity
@Table(name = "t_extract_task")
public class ExtractTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;
    private String description;
    private Long sourceDatasourceId;
    private Long targetDatasourceId;
    private String sourceTable;
    private String targetTable;
    private String extractMode;
    private String incrementField;

    @Column(length = 2000)
    private String querySql;

    private String writeMode;
    private String filterCondition;
    private String splitPk;
    private Integer splitPkNum = 1;
    private Integer status = 0;
    private String cronExpression;
    private LocalDateTime lastRunTime;
    private Integer lastRunStatus;
    private Long lastRunDuration;
    private Long lastRunRecords;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
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
