package com.example.taqsit.repository;

import com.example.taqsit.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(String name);

    List<Permission> findByIsCyberArk(Integer isCyberArk);
}