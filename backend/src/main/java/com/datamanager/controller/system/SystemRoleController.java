package com.datamanager.controller.system;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.Role;
import com.datamanager.model.RolePermission;
import com.datamanager.repository.RolePermissionRepository;
import com.datamanager.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/system/role")
public class SystemRoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @GetMapping
    public ApiResponse<Page<Role>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Role> roles = roleRepository.findAll(pageable);
        return ApiResponse.success(roles);
    }

    @PostMapping
    public ApiResponse<Void> saveRole(@RequestBody Role role) {
        if (role.getId() == null) {
            Optional<Role> existing = roleRepository.findByCode(role.getCode());
            if (existing.isPresent()) {
                return ApiResponse.error(400, "角色编码已存在");
            }
        }
        roleRepository.save(role);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(id)
                .stream().map(RolePermission::getPermissionId).toList();
        return ApiResponse.success(permissionIds);
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> updateRolePermissions(
            @PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> permissionIds = body.get("permissionIds");
        rolePermissionRepository.deleteByRoleId(id);
        if (permissionIds != null) {
            for (Long permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(id);
                rp.setPermissionId(permId);
                rolePermissionRepository.save(rp);
            }
        }
        return ApiResponse.success(null);
    }
}
