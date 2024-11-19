package com.example.taqsit.service;

import com.example.taqsit.dto.RoleDto;
import com.example.taqsit.entity.Permission;
import com.example.taqsit.entity.Role;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.repository.PermissionRepository;
import com.example.taqsit.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public HttpEntity<?> createOrEditRole(RoleDto dto) {
        try {
            Map<String, Object> valid = valid(dto);
            if (!valid.isEmpty()) {
                return AllApiResponse.response(422, 0, "Validator errors!", valid);
            }
            Role role;
            if (dto.getId() != null) {
                role = roleRepository.findByIdAndDeletedFalse(dto.getId()).orElse(null);
                if (role == null) return AllApiResponse.response(404, 0, "Role not found!");
            } else {
                role = new Role();
            }
            List<Permission> allById = permissionRepository.findAllById(dto.getPermissions());
            if (allById.size() == 0)
                return AllApiResponse.response(422, 0, "The number of permissions must be at least one");
            role.setName(dto.getRoleName());
            role.setPrettyName(dto.getRolePrettyName());
            role.setPermission(allById);
            roleRepository.save(role);
            return AllApiResponse.response(1, "Role created/updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error create or edit role", e.getMessage());
        }
    }

    public HttpEntity<?> getAll() {
        try {
            List<Role> all = roleRepository.findAllByDeletedFalse();
            return AllApiResponse.response(1, "Roles", all.stream().map(Role::toDto).toList());
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error get all roles!", e.getMessage());
        }
    }

    public HttpEntity<?> deleteRole(Integer id) {
        try {
            Role role = roleRepository.findByIdAndDeletedFalse(id).orElse(null);
            if (role != null && !Objects.equals(role.getName(), "admin")) {
                role.setDeleted(true);
                roleRepository.save(role);
                return AllApiResponse.response(1, "Role deleted successfully!");
            } else return AllApiResponse.response(404, 1, "Role not found!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error delete role!", e.getMessage());
        }
    }

    public HttpEntity<?> getAllPermissions(Integer isCyberArk) {
        try {
            List<Permission> all = permissionRepository.findAll();
            if (isCyberArk != null)
                all = all.stream().filter(permission -> Objects.equals(permission.getIsCyberArk(), isCyberArk)).toList();
            Map<String, List<Permission>> collect = all.stream().collect(Collectors.groupingBy(item -> item.getCategoryName().toLowerCase().replaceAll(" ", "_")));
            return AllApiResponse.response(1, "All permissions!", collect);
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error get all permissions!", e.getMessage());
        }
    }

    public Map<String, Object> valid(RoleDto dto) {
        Map<String, Object> errors = new HashMap<>();
        if (dto.getRoleName() == null) {
            errors.put("roleName", "The field must not be empty!");
        } else if (dto.getId() == null && roleRepository.existsByName(dto.getRoleName())) {
            errors.put("roleName", "This role name already exists!");
        } else if (dto.getId() != null && Objects.equals(dto.getRoleName(), "admin")) {
            errors.put("roleName", "Editing of this role is not permitted!");
        } else if (dto.getId() != null && dto.getRoleName() != null) {
            Optional<Role> byName = roleRepository.findByName(dto.getRoleName());
            if (byName.isPresent() && !Objects.equals(byName.get().getId(), dto.getId())) {
                errors.put("roleName", "This role name already exists!");
            }
        }
        if (dto.getRolePrettyName() == null) {
            errors.put("rolePrettyName", "The field must not be empty!");
        }
        if (dto.getPermissions() == null || dto.getPermissions().size() == 0) {
            errors.put("permissions", "The field must not be empty!");
        }
        return errors;
    }
}
