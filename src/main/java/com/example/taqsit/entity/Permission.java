package com.example.taqsit.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity implements Serializable {
    private String prettyName;

    private String categoryName;

    private String categoryPrettyName;
    @Column(unique = true)
    private String name;

    private Integer isCyberArk;

    public Permission(String name) {
        this.name = name;
    }

    public Permission(String prettyName, String categoryName, String name) {
        this.prettyName = prettyName;
        this.categoryName = categoryName;
        this.name = name;
    }

    public Permission(String prettyName, String categoryName, String categoryPrettyName, String name) {
        this.prettyName = prettyName;
        this.categoryName = categoryName;
        this.categoryPrettyName = categoryPrettyName;
        this.name = name;
    }

    public Map<String, Object> toDto(){
        return new HashMap<>(){{
            put("id", getId());
            put("name", getName());
            put("categoryName", getCategoryName());
            put("categoryPrettyName", getCategoryPrettyName());
            put("prettyName", getPrettyName());
        }};
    }
}
