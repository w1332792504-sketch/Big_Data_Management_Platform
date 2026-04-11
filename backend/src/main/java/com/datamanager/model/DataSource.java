package com.datamanager.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据源配置表
 */
@Data
@Entity
@Table(name = "t_datasource")
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型：DM(达梦), MYSQL, HIVE, ORACLE 等
     */
    private String type;

    /**
     * JDBC URL
     */
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 (加密存储)
     */
    private String password;

    /**
     * 数据库名/Schema
     */
    @Column(name = "db_name")
    private String database;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = 1;

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
