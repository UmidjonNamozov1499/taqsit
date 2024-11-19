package com.example.taqsit.repository;

import com.example.taqsit.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Optional<Role> findByIdAndDeletedFalse(Integer id);

    List<Role> findAllByDeletedFalse();
}
