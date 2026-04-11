package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.dto.DataSourceDTO;
import com.datamanager.model.DataSource;
import com.datamanager.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/datasource")
@CrossOrigin(origins = "*")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @GetMapping("/list")
    public ApiResponse<List<DataSourceDTO>> list() {
        return ApiResponse.success(dataSourceService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<DataSourceDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(dataSourceService.findById(id));
    }

    @PostMapping("/save")
    public ApiResponse<DataSource> save(@RequestBody DataSourceDTO dto) {
        return ApiResponse.success(dataSourceService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dataSourceService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/test")
    public ApiResponse<Boolean> testConnection(@RequestBody DataSourceDTO dto) {
        return ApiResponse.success(dataSourceService.testConnection(dto));
    }
}
