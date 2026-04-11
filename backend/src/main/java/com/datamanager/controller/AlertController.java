package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.AlertRecord;
import com.datamanager.model.AlertRule;
import com.datamanager.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警控制器
 */
@RestController
@RequestMapping("/api/alert")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    // 告警规则管理
    @GetMapping("/rules")
    public ApiResponse<List<AlertRule>> listRules() {
        return ApiResponse.success(alertService.findAllRules());
    }

    @GetMapping("/rules/active")
    public ApiResponse<List<AlertRule>> listActiveRules() {
        return ApiResponse.success(alertService.findActiveRules());
    }

    @GetMapping("/rules/{id}")
    public ApiResponse<AlertRule> getRule(@PathVariable Long id) {
        return ApiResponse.success(alertService.findRuleById(id));
    }

    @PostMapping("/rules")
    public ApiResponse<AlertRule> createRule(@RequestBody AlertRule rule) {
        return ApiResponse.success(alertService.saveRule(rule));
    }

    @PutMapping("/rules/{id}")
    public ApiResponse<AlertRule> updateRule(@PathVariable Long id, @RequestBody AlertRule rule) {
        rule.setId(id);
        return ApiResponse.success(alertService.saveRule(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        alertService.deleteRule(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/rules/{id}/status")
    public ApiResponse<AlertRule> updateRuleStatus(@PathVariable Long id, @RequestParam Integer status) {
        return ApiResponse.success(alertService.updateRuleStatus(id, status));
    }

    // 发送告警
    @PostMapping("/send/{ruleId}")
    public ApiResponse<AlertRecord> sendAlert(
            @PathVariable Long ruleId,
            @RequestParam String content) {
        return ApiResponse.success(alertService.sendAlert(ruleId, content));
    }

    // 告警记录查询
    @GetMapping("/records")
    public ApiResponse<Page<AlertRecord>> listRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(alertService.findRecords(page, size));
    }

    @GetMapping("/records/pending")
    public ApiResponse<Page<AlertRecord>> listPendingRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(alertService.findPendingRecords(page, size));
    }

    // 统计
    @GetMapping("/stats/pending-count")
    public ApiResponse<Long> getPendingCount() {
        return ApiResponse.success(alertService.countPendingAlerts());
    }

    @GetMapping("/stats/critical-count")
    public ApiResponse<Long> getCriticalCount() {
        return ApiResponse.success(alertService.countCriticalPendingAlerts());
    }

    // 告警确认
    @PutMapping("/records/{id}/acknowledge")
    public ApiResponse<AlertRecord> acknowledge(
            @PathVariable Long id,
            @RequestParam String acknowledgedBy) {
        return ApiResponse.success(alertService.acknowledge(id, acknowledgedBy));
    }

    // 告警解决
    @PutMapping("/records/{id}/resolve")
    public ApiResponse<AlertRecord> resolve(
            @PathVariable Long id,
            @RequestParam String resolvedBy,
            @RequestParam(required = false) String remark) {
        return ApiResponse.success(alertService.resolve(id, resolvedBy, remark));
    }
}
