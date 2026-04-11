package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse<String> home() {
        return ApiResponse.success("大数据管理平台后端服务正在运行");
    }
}
