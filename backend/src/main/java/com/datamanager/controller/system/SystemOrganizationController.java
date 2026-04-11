package com.datamanager.controller.system;

import com.datamanager.dto.ApiResponse;
import com.datamanager.model.Organization;
import com.datamanager.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/organization")
public class SystemOrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping
    public ApiResponse<List<Organization>> getOrganizations(
            @RequestParam(required = false) String name) {
        List<Organization> organizations;
        if (name != null && !name.isEmpty()) {
            organizations = organizationRepository.findByNameContaining(name);
        } else {
            organizations = organizationRepository.findAll();
        }
        return ApiResponse.success(organizations);
    }

    @PostMapping
    public ApiResponse<Void> saveOrganization(@RequestBody Organization organization) {
        organizationRepository.save(organization);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrganization(@PathVariable Long id) {
        organizationRepository.deleteById(id);
        return ApiResponse.success(null);
    }
}
