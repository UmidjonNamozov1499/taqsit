package com.example.taqsit.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private Integer id;

    private String roleName;

    private String rolePrettyName;

    private List<Integer> permissions;

}
