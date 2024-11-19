package com.example.taqsit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Column(columnDefinition = "text", length = 1500)
    private String text;

    @ManyToOne
    private User user;

    private Long likes;

    @OneToOne
    @JsonIgnore
    private FileCatalog fileCatalog;

    public Map<String,Object> toDto(){
        return new HashMap<>(){{
            put("text",text);
            put("likes",likes);
            put("fileCatalog",fileCatalog.toDto());
        }};
    }
}
