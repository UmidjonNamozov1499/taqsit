package com.example.taqsit.controller;

import com.example.taqsit.dto.PostDto;
import com.example.taqsit.dto.UserDto;
import com.example.taqsit.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin
public class PostController {
    private final PostService postService;

    @PostMapping
    public HttpEntity<?> create(@ModelAttribute PostDto dto) {
        return postService.postCreate(dto);
    }

    @PutMapping
    public HttpEntity<?> update(@ModelAttribute PostDto dto) {
        return postService.postEdit(dto);
    }

    @GetMapping("/getAuthView")
    public HttpEntity<?> get(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return postService.postViewUser(page, size);
    }

    @PreAuthorize("hasAnyAuthority('get_all_posts')")
    @GetMapping("/getAll")
    public HttpEntity<?> getAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return postService.postList(page, size);
    }
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Integer id) {
        return postService.postDelete(id);
    }
}
