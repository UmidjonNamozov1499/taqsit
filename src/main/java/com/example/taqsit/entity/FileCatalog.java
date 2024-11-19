package com.example.taqsit.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class FileCatalog extends BaseEntity {
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Integer> fileList;

    private Integer generalFile;

    public Map<String, Object> toDto() {
        return new HashMap<>() {{
            put("id", getId());
            put("fileList", fileList);
            put("generalFile", generalFile);
        }};
    }
}
