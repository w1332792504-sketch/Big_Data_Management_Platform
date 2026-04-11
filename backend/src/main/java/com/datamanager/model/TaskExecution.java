package com.datamanager.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 任务执行记录表
 */
@Data
@Entity
@Table(name = "t_task_execution")
public class TaskExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务 ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 执行类型：MANUAL(手动), SCHEDULE(定时)
     */
    private String executionType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时 (秒)
     */
    private Long duration;

    /**
     * 执行状态：RUNNING, SUCCESS, FAILED
     */
    private String status;

    /**
     * 源表记录数
     */
    private Long sourceCount;

    /**
     * 目标表记录数 (执行后)
     */
    private Long targetCount;

    /**
     * 抽取记录数
     */
    private Long extractCount;

    /**
     * 错误信息
     */
    @Column(length = 2000)
    private String errorMsg;

    /**
     * DataX 日志路径
     */
    private String logPath;

    /**
     * 操作人
     */
    private String operator;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
