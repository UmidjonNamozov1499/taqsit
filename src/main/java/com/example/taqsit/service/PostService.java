package com.example.taqsit.service;

import com.example.taqsit.dto.PostDto;
import com.example.taqsit.entity.FileCatalog;
import com.example.taqsit.entity.Post;
import com.example.taqsit.entity.User;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.payload.ResponseData;
import com.example.taqsit.repository.PostRepository;
import com.example.taqsit.repository.UserRepository;
import javafx.geometry.Pos;
import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FileService fileService;

    public HttpEntity<?> postCreate(PostDto dto) {
        try {
            Map<String, Object> valid = dto.valid();
            if (valid.size() > 0) {
                return AllApiResponse.response(422, 0, "Validation Failed", valid);
            }
            User user = userService.currentUser();
            if (user == null) {
                return AllApiResponse.response(409, 0, "Not authentication user");
            }
            Post post = new Post();
            post.setUser(user);
            post.setText(dto.getText());
            FileCatalog fileCatalog = fileService.getFileCatalog(dto.getGeneralFile(), dto.getFiles(), post.getFileCatalog(), dto.getOldFiles());
            post.setFileCatalog(fileCatalog);
            postRepository.save(post);
            return AllApiResponse.response(200, "Successfully created!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, e.getMessage());
        }
    }

    public HttpEntity<?> postEdit(PostDto dto) {
        try {
            User user = userService.currentUser();
            Post post = postRepository.findById(dto.getId()).orElse(null);
            if (!post.getUser().getId().equals(user.getId())) {
                return AllApiResponse.response(409, 0, "Not authentication user");
            }
            if (post == null) {
                return AllApiResponse.response(404, 0, "Post not found");
            }
            Post postEntity = dto.toEntity(post);
            if (dto.getFileCategoryId() != null && dto.getFileCategoryId().equals(post.getFileCatalog().getId())) {
                postEntity.setFileCatalog(post.getFileCatalog());
            }
            FileCatalog fileCatalog = fileService.getFileCatalog(dto.getGeneralFile(), dto.getFiles(), post.getFileCatalog(), dto.getOldFiles());
            postEntity.setFileCatalog(fileCatalog);
            postRepository.save(postEntity);
            return AllApiResponse.response(200, "Successfully updated!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, e.getMessage());
        }
    }

    public HttpEntity<?> postViewUser(int page, int size) {
        try {
            User user = userService.currentUser();
            if (user == null) {
                return AllApiResponse.response(409, 0, "Not authentication user");
            }
            Page<Post> posts = postRepository.findAllByUser(user,PageRequest.of(page-1,size));
            return AllApiResponse.response(200, "Successfully viewed posts", new ResponseData(posts));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, e.getMessage());
        }
    }
    public HttpEntity<?> postList(int page, int size) {
        try {
            User user = userService.currentUser();
            if (user == null) {
                return AllApiResponse.response(409, 0, "Not authentication user");
            }
            Page<Post> posts = postRepository.findAll(PageRequest.of(page-1,size));

            return AllApiResponse.response(200, "Successfully viewed posts", new ResponseData(posts));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, e.getMessage());
        }
    }

    public HttpEntity<?> postDelete(Integer id) {
        try {
            User user = userService.currentUser();
            if (user == null) {
                return AllApiResponse.response(404, 0, "Not authentication user");
            }
            Post post = postRepository.findById(id).orElse(null);
            if (post == null) {
                return AllApiResponse.response(404, 0, "Post not found");
            }
            postRepository.delete(post);
            return AllApiResponse.response(200, "Successfully deleted posts");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, e.getMessage());
        }
    }
}
