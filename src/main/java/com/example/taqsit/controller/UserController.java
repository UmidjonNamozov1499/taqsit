package com.example.taqsit.controller;

import com.example.taqsit.dto.UserDto;
import com.example.taqsit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('create_user')")
    @PostMapping
    public HttpEntity<?> create(@ModelAttribute UserDto dto) {
        dto.setId(null);
        return userService.createOrEdit(dto);
    }

    @PreAuthorize("hasAuthority('update_user')")
    @PutMapping("/{id}")
    public HttpEntity<?> update(@PathVariable Integer id, @ModelAttribute UserDto dto) {
        dto.setId(id);
        return userService.createOrEdit(dto);
    }

    @PreAuthorize("hasAuthority('get_all_users')")
    @GetMapping
    public HttpEntity<?> getAllUsers(@RequestParam(required = false, defaultValue = "1") int page,
                                     @RequestParam(required = false, defaultValue = "10") int size,
                                     @RequestParam(required = false) String search) {
        return userService.getAllUsers(page, size, search);
    }

    @PreAuthorize("hasAuthority('get_one_user')")
    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Integer id) {
        return userService.getOneUser(id);
    }

    @PreAuthorize("hasAuthority('delete_user')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }
}
