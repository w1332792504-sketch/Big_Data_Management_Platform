package com.datamanager.dto;

import lombok.Data;

@Data
public class DataSourceDTO {
    private Long id;
    private String name;
    private String type;
    private String jdbcUrl;
    private String username;
    private String password;
    private String database;
    private String host;
    private Integer port;
    private String description;
    private Integer status;
}
