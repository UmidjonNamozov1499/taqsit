package com.example.taqsit.dto;

import com.example.taqsit.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Integer id;
    private String text;
    private MultipartFile generalFile;
    private List<MultipartFile> files;
    private List<Integer> oldFiles;
    private Integer fileCategoryId;

    public Map<String, Object> valid() {
        return new HashMap<>() {{
            if (text == null || text.length() == 0) {
                put("text", "Field is required");
            }
            if (generalFile == null || generalFile.isEmpty()) {
                put("generalFile", "Field is required");
            }
            if (files != null && files.size() > 0) {
                List<MultipartFile> image = files.stream().filter(f -> f.getContentType() == null || !f.getContentType().startsWith("image")).toList();
                if (image.size() > 0) {
                    put("file", "File is not match image!");
                }
            }
        }};
    }
    public Post toEntity(Post post) {
        post.setId(id);
        post.setText(text);
        return post;
    }
}
