package com.example.taqsit.dto;

import com.example.taqsit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Integer id;

    private String lastName;

    private String firstName;

    private String middleName;

    private String username;

    private String password;
    private Set<Integer> roles;


    public Map<String, Object> valid() {
        Map<String, Object> errors = new HashMap<>();
        if (username == null) {
            errors.put("username", "Field is required!");
        }
        if (lastName == null) {
            errors.put("lastName", "Field is required!");
        }
        if (firstName == null) {
            errors.put("firstName", "Field is required!");
        }
        if (id == null && password == null) {
            errors.put("password", "Field is required!");
        }
        if (roles == null || roles.isEmpty()) {
            errors.put("roles", "Field is required!");
        }
        return errors;
    }
    public Map<String, Object> validRegister() {
        Map<String, Object> errors = new HashMap<>();
        if (username == null) {
            errors.put("username", "Field is required!");
        }
        if (lastName == null) {
            errors.put("lastName", "Field is required!");
        }
        if (firstName == null) {
            errors.put("firstName", "Field is required!");
        }
        if (id == null && password == null) {
            errors.put("password", "Field is required!");
        }
        return errors;
    }

    public User dtoToEntity(User user) {
        user.setUsername(username);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        return user;
    }
}
