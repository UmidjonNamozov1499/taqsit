package com.example.taqsit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements Serializable {
    private String name;

    private String prettyName;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permission;

    @JsonIgnore
    private boolean deleted = false;

    public Map<String, Object> toDto() {
        Map<String, List<Permission>> collect = new HashMap<>();
        if (permission != null) {
            collect = permission.stream().collect(Collectors.groupingBy(item -> item.getCategoryName().toLowerCase().replaceAll(" ", "_")));
        }
        Map<String, List<Permission>> finalCollect = collect;
        return new HashMap<>() {{
            put("id", getId());
            put("name", name);
            put("prettyName", prettyName);
            put("permissions", finalCollect);
        }};

    }

    public Map<String, Object> toDtoListPerm() {
        return new HashMap<>() {{
            put("id", getId());
            put("name", name);
            put("prettyName", prettyName);
            put("permissions", permission != null ? permission.stream().map(Permission::getName) : List.of());
        }};
    }

    public Role(String name) {
        this.name = name;
    }

    public Role(String name, String prettyName) {
        this.name = name;
        this.prettyName = prettyName;
    }
}
