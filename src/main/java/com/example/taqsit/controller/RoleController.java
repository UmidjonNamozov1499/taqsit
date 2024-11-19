package com.example.taqsit.controller;

import com.example.taqsit.dto.RoleDto;
import com.example.taqsit.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@CrossOrigin
public class RoleController {
    private final RoleService roleService;


    @GetMapping
    @PreAuthorize("hasAuthority('get_all_role')")
    public HttpEntity<?> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('get_all_permissions')")
    public HttpEntity<?> getAllPermissions(
            @RequestParam(name = "isCyberArk", required = false) Integer isCyberArk
    ) {
        return roleService.getAllPermissions(isCyberArk);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create_role')")
    public HttpEntity<?> createRole(@ModelAttribute RoleDto roleDto) {
        roleDto.setId(null);
        return roleService.createOrEditRole(roleDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update_role')")
    public HttpEntity<?> updateRole(@PathVariable Integer id, @ModelAttribute RoleDto roleDto) {
        roleDto.setId(id);
        return roleService.createOrEditRole(roleDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete_role')")
    public HttpEntity<?> deleteRole(@PathVariable Integer id) {
        return roleService.deleteRole(id);
    }
}
