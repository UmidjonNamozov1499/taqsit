package com.example.taqsit.controller;

import com.example.taqsit.entity.User;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.secret.CurrentUser;
import com.example.taqsit.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@CrossOrigin
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<?> saveAvatar(@CurrentUser User user, MultipartFile file) {
        if (user != null) {
            return fileService.saveAvatar(file, user);
        } else {
            return AllApiResponse.response(403, 0, "Error auth!");
        }
    }

    @GetMapping(value = "/get-file/{id}")
    public HttpEntity<?> getFile(@PathVariable Integer id) {
        return fileService.getFile(id);
    }
}
