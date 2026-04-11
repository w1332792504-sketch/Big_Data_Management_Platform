package com.datamanager.dto;

import lombok.Data;

@Data
public class ExtractTaskDTO {
    private Long id;
    private String taskName;
    private String description;
    private Long sourceDatasourceId;
    private String sourceDatasourceName;
    private Long targetDatasourceId;
    private String targetDatasourceName;
    private String sourceTable;
    private String targetTable;
    private String extractMode;
    private String incrementField;
    private String querySql;
    private String writeMode;
    private String filterCondition;
    private Integer splitPkNum;
    private String splitPk;
    private Integer status;
    private String cronExpression;
    private Long lastRunTime;
    private Integer lastRunStatus;
    private Long lastRunDuration;
    private Long lastRunRecords;
}
