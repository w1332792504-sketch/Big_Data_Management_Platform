package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.dto.LoginRequest;
import com.datamanager.dto.UserDTO;
import com.datamanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<UserDTO> login(@RequestBody LoginRequest request) {
        UserDTO user = userService.login(request);
        return ApiResponse.success(user);
    }

    @GetMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/current")
    public ApiResponse<UserDTO> getCurrent(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.error(401, "未登录");
        }
        // 从 token 中解析用户名（简单实现）
        String username = token.replace("token_", "").split("_")[0];
        UserDTO user = userService.getCurrentUser(username);
        user.setToken(token);
        return ApiResponse.success(user);
    }

    @PostMapping("/check")
    public ApiResponse<Boolean> check(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ApiResponse.success(false);
        }
        return ApiResponse.success(true);
    }
}
