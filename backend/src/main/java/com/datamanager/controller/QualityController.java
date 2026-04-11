package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.QualityCheckResult;
import com.datamanager.model.QualityRule;
import com.datamanager.service.QualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据质量控制器
 */
@RestController
@RequestMapping("/api/quality")
@CrossOrigin(origins = "*")
public class QualityController {

    @Autowired
    private QualityService qualityService;

    // 规则管理
    @GetMapping("/rules")
    public ApiResponse<List<QualityRule>> listRules() {
        return ApiResponse.success(qualityService.findAllRules());
    }

    @GetMapping("/rules/datasource/{datasourceId}")
    public ApiResponse<List<QualityRule>> listRulesByDatasource(@PathVariable Long datasourceId) {
        return ApiResponse.success(qualityService.findRulesByDatasource(datasourceId));
    }

    @GetMapping("/rules/{id}")
    public ApiResponse<QualityRule> getRule(@PathVariable Long id) {
        return ApiResponse.success(qualityService.findRuleById(id));
    }

    @PostMapping("/rules")
    public ApiResponse<QualityRule> createRule(@RequestBody QualityRule rule) {
        return ApiResponse.success(qualityService.saveRule(rule));
    }

    @PutMapping("/rules/{id}")
    public ApiResponse<QualityRule> updateRule(@PathVariable Long id, @RequestBody QualityRule rule) {
        rule.setId(id);
        return ApiResponse.success(qualityService.saveRule(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        qualityService.deleteRule(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/rules/{id}/status")
    public ApiResponse<QualityRule> updateRuleStatus(@PathVariable Long id, @RequestParam Integer status) {
        return ApiResponse.success(qualityService.updateRuleStatus(id, status));
    }

    // 执行检查
    @PostMapping("/rules/{id}/execute")
    public ApiResponse<QualityCheckResult> executeCheck(@PathVariable Long id) {
        return ApiResponse.success(qualityService.executeCheck(id));
    }

    @PostMapping("/rules/execute-all")
    public ApiResponse<Void> executeAllChecks() {
        qualityService.executeAllActiveRules();
        return ApiResponse.success(null);
    }

    // 检查结果
    @GetMapping("/results")
    public ApiResponse<Page<QualityCheckResult>> listResults(
            @RequestParam Long datasourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(qualityService.findResults(datasourceId, page, size));
    }

    @GetMapping("/results/rule/{ruleId}")
    public ApiResponse<List<QualityCheckResult>> listResultsByRule(@PathVariable Long ruleId) {
        return ApiResponse.success(qualityService.findResultsByRule(ruleId));
    }

    // 统计
    @GetMapping("/stats/fail-count")
    public ApiResponse<Long> getFailCount() {
        return ApiResponse.success(qualityService.countFailChecks());
    }
}
