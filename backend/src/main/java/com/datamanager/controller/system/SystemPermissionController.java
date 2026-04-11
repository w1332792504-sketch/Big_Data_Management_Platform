package com.datamanager.controller.system;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.Permission;
import com.datamanager.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
public class SystemPermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping
    public ApiResponse<List<Permission>> getPermissions(
            @RequestParam(required = false) String name) {
        List<Permission> permissions;
        if (name != null && !name.isEmpty()) {
            permissions = permissionRepository.findByNameContaining(name);
        } else {
            permissions = permissionRepository.findAll();
        }
        return ApiResponse.success(permissions);
    }

    @PostMapping
    public ApiResponse<Void> savePermission(@RequestBody Permission permission) {
        permissionRepository.save(permission);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return ApiResponse.success(null);
    }
}
