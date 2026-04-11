package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.AuditLog;
import com.datamanager.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志控制器
 */
@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 查询审计日志
     */
    @GetMapping("/logs")
    public ApiResponse<Page<AuditLog>> listLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(auditService.findLogs(page, size));
    }

    /**
     * 按模块查询
     */
    @GetMapping("/logs/module/{module}")
    public ApiResponse<Page<AuditLog>> listByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(auditService.findByModule(module, page, size));
    }

    /**
     * 按操作人查询
     */
    @GetMapping("/logs/operator/{operator}")
    public ApiResponse<Page<AuditLog>> listByOperator(
            @PathVariable String operator,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(auditService.findByOperator(operator, page, size));
    }

    /**
     * 按时间范围查询
     */
    @GetMapping("/logs/time-range")
    public ApiResponse<Page<AuditLog>> listByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(auditService.findByTimeRange(start, end, page, size));
    }

    /**
     * 统计今日操作数
     */
    @GetMapping("/stats/today")
    public ApiResponse<Long> countToday() {
        return ApiResponse.success(auditService.countTodayOperations());
    }

    /**
     * 按模块统计
     */
    @GetMapping("/stats/by-module")
    public ApiResponse<List<Object[]>> countByModule() {
        return ApiResponse.success(auditService.countByModule());
    }
}
