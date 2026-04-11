package com.datamanager.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 元数据实体
 */
@Data
@Entity
@Table(name = "metadata")
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long datasourceId;

    @Column(length = 100)
    private String databaseName;

    @Column(length = 100)
    private String tableName;

    @Column(length = 100)
    private String columnName;

    @Column(length = 50)
    private String columnType;

    private Integer columnLength;

    private Integer columnPrecision;

    private Integer columnScale;

    @Column(nullable = false)
    private Boolean isNullable;

    @Column(nullable = false)
    private Boolean isPrimaryKey;

    private String defaultValue;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(length = 50)
    private String objectType; // DATABASE, TABLE, COLUMN

    private Integer rowCount;

    private LocalDateTime lastSyncTime;

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
