package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.dto.ExtractTaskDTO;
import com.datamanager.model.ExtractTask;
import com.datamanager.model.TaskExecution;
import com.datamanager.service.ExtractTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "*")
public class ExtractTaskController {

    @Autowired
    private ExtractTaskService extractTaskService;

    @GetMapping("/list")
    public ApiResponse<List<ExtractTaskDTO>> list() {
        return ApiResponse.success(extractTaskService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ExtractTaskDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(extractTaskService.findById(id));
    }

    @PostMapping("/save")
    public ApiResponse<ExtractTask> save(@RequestBody ExtractTaskDTO dto) {
        return ApiResponse.success(extractTaskService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        extractTaskService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/run")
    public ApiResponse<Long> runTask(@PathVariable Long id,
                                      @RequestParam(defaultValue = "MANUAL") String executionType,
                                      @RequestParam(defaultValue = "admin") String operator) {
        return ApiResponse.success(extractTaskService.runTask(id, executionType, operator));
    }

    @PostMapping("/{id}/stop")
    public ApiResponse<Void> stopTask(@PathVariable Long id) {
        extractTaskService.stopTask(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/executions")
    public ApiResponse<Page<TaskExecution>> getExecutions(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(extractTaskService.findExecutions(id, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}/latest-execution")
    public ApiResponse<TaskExecution> getLatestExecution(@PathVariable Long id) {
        return ApiResponse.success(extractTaskService.findLatestExecution(id));
    }
}
