package com.datamanager.controller.system;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.User;
import com.datamanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/system/user")
public class SystemUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

        Page<User> users;
        if (username != null && !username.isEmpty() && status != null) {
            users = userRepository.findByUsernameContainingAndStatus(username, status, pageable);
        } else if (username != null && !username.isEmpty()) {
            users = userRepository.findByUsernameContaining(username, pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(status, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return ApiResponse.success(users);
    }

    @PostMapping
    public ApiResponse<Void> saveUser(@RequestBody User user) {
        if (user.getId() == null) {
            Optional<User> existing = userRepository.findByUsername(user.getUsername());
            if (existing.isPresent()) {
                return ApiResponse.error(400, "用户名已存在");
            }
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ApiResponse.success(null);
    }
}
