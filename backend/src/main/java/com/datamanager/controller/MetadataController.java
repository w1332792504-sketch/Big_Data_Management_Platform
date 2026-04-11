package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.Metadata;
import com.datamanager.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 元数据控制器
 */
@RestController
@RequestMapping("/api/metadata")
@CrossOrigin(origins = "*")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    /**
     * 同步数据源元数据
     */
    @PostMapping("/sync/{datasourceId}")
    public ApiResponse<Void> syncMetadata(@PathVariable Long datasourceId) {
        metadataService.syncMetadata(datasourceId);
        return ApiResponse.success(null);
    }

    /**
     * 获取数据源的所有数据库
     */
    @GetMapping("/databases/{datasourceId}")
    public ApiResponse<List<String>> getDatabases(@PathVariable Long datasourceId) {
        return ApiResponse.success(metadataService.getDatabases(datasourceId));
    }

    /**
     * 获取数据库的所有表
     */
    @GetMapping("/tables/{datasourceId}")
    public ApiResponse<List<String>> getTables(
            @PathVariable Long datasourceId,
            @RequestParam String databaseName) {
        return ApiResponse.success(metadataService.getTables(datasourceId, databaseName));
    }

    /**
     * 获取表的所有列
     */
    @GetMapping("/columns/{datasourceId}")
    public ApiResponse<List<Metadata>> getColumns(
            @PathVariable Long datasourceId,
            @RequestParam String databaseName,
            @RequestParam String tableName) {
        return ApiResponse.success(metadataService.getColumns(datasourceId, databaseName, tableName));
    }

    /**
     * 获取数据源的所有元数据
     */
    @GetMapping("/list/{datasourceId}")
    public ApiResponse<List<Metadata>> getMetadataByDatasource(@PathVariable Long datasourceId) {
        return ApiResponse.success(metadataService.getMetadataByDatasource(datasourceId));
    }

    /**
     * 获取表级元数据
     */
    @GetMapping("/tables-level/{datasourceId}")
    public ApiResponse<List<Metadata>> getTableMetadata(@PathVariable Long datasourceId) {
        return ApiResponse.success(metadataService.getTableMetadata(datasourceId));
    }

    /**
     * 获取单个元数据
     */
    @GetMapping("/{id}")
    public ApiResponse<Metadata> getMetadata(@PathVariable Long id) {
        return ApiResponse.success(metadataService.findById(id));
    }

    /**
     * 更新元数据注释
     */
    @PutMapping("/{id}/comment")
    public ApiResponse<Metadata> updateComment(
            @PathVariable Long id,
            @RequestParam String comment) {
        return ApiResponse.success(metadataService.updateComment(id, comment));
    }
}
