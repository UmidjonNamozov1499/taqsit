package com.example.taqsit.components;

import com.example.taqsit.entity.Permission;
import com.example.taqsit.entity.Role;
import com.example.taqsit.entity.User;
import com.example.taqsit.repository.PermissionRepository;
import com.example.taqsit.repository.RoleRepository;
import com.example.taqsit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddlAuto;

    @Value("${spring.sql.init.mode}")
    String initMode;

    @Value("${file.upload.url}")
    private String url;

    @Override
    public void run(String... args) {
        try {
            List<Permission> permissions = List.of(
                    new Permission("Shaxsiy ma'lumotlarni olish", "Auth controller", "Avtorizatsiya", "get_current_user"),
                    new Permission("Kirish urunishlarini o'chirish", "Auth controller", "Avtorizatsiya", "remove_user_filed_attempts"),
                    new Permission("Kirish urunishlarini olish", "Auth controller", "Avtorizatsiya", "get_user_filed_attempts"),

                    new Permission("Rol yaratish", "Role controller", "Ruxsatlar", "create_role"),
                    new Permission("Rollarni olish", "Role controller", "Ruxsatlar", "get_all_role"),
                    new Permission("Ruxsatlarni olish", "Role controller", "Ruxsatlar", "get_all_permissions"),
                    new Permission("Rolni tahrirlash", "Role controller", "Ruxsatlar", "update_role"),
                    new Permission("Rolni o'chirish", "Role controller", "Ruxsatlar", "delete_role"),

                    new Permission("Foydalanuvchilarni olish", "User controller", "Foydalanuvchilar", "get_all_users"),
                    new Permission("Foydalanuvchi yaratish", "User controller", "Foydalanuvchilar", "create_user"),
                    new Permission("Foydalanuvchini olish", "User controller", "Foydalanuvchilar", "get_one_user"),
                    new Permission("Foydalanuvchini tahrirlash", "User controller", "Foydalanuvchilar", "update_user"),
                    new Permission("Foydalanuvchini o'chirish", "User controller", "Foydalanuvchilar", "delete_user"),

                    new Permission("Postlarni olish", "Post controller", "posts", "get_all_posts")
            );

            List<Role> roles = List.of(
                    new Role("admin", "Admin"),
                    new Role("customer","Customer")
            );

            List<Permission> list = permissions.stream().map(item -> {
                Optional<Permission> byName = permissionRepository.findByName(item.getName());
                return byName.orElseGet(() -> permissionRepository.save(item));
            }).toList();

            Set<Role> roleList = roles.stream().map(item -> {
                Optional<Role> byName = roleRepository.findByName(item.getName());
                if (byName.isPresent()) {
                    Role role = byName.get();
                    if (role.getName().equals("admin")) {
                        role.setPermission(list);
                        role = roleRepository.save(role);
                    }else {
                        role.setPermission(list.stream().filter(p->p.getName().equals("get_all_posts")).toList());
                    }
                    return role;
                } else {
                    if (item.getName().equals("admin")) {
                        item.setPermission(list);
                    }else {
                        item.setPermission(list.stream().filter(p->p.getName().equals("get_all_posts")).toList());
                    }
                    return roleRepository.save(item);
                }
            }).collect(Collectors.toSet());
            Optional<User> admin123 = userRepository.findByUsernameAndDeletedFalse("admin_123");
            if (admin123.isEmpty()) {
                Set<Role> admin = roleList.stream().filter(item -> item.getName().equals("admin")).collect(Collectors.toSet());
                userRepository.save(new User("Admin", "Admin", "Admin", "admin_123", passwordEncoder.encode("password"), admin));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
