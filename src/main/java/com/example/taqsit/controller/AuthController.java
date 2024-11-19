package com.example.taqsit.controller;


import com.example.taqsit.dto.UserDto;
import com.example.taqsit.entity.User;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.payload.LoginRequest;
import com.example.taqsit.payload.SetPasswordRequest;
import com.example.taqsit.secret.CurrentUser;
import com.example.taqsit.service.BruteForceService;
import com.example.taqsit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
    private final UserService userService;
    private final BruteForceService bruteForceService;

    @PostMapping("/register")
    public HttpEntity<?> register(@ModelAttribute UserDto dto) {
        dto.setId(null);
        return userService.register(dto);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@ModelAttribute LoginRequest loginRequest) {
        if (loginRequest != null && loginRequest.getPassword() != null && loginRequest.getUsername() != null) {
            return userService.signIn(loginRequest);
        } else {
            return AllApiResponse.response(422, 0, "Username and password is required!");
        }
    }

    @PreAuthorize("hasAuthority('get_current_user')")
    @GetMapping("/current-user")
    public HttpEntity<?> getCurrent(@CurrentUser User user) {
        if (user != null) {
            return AllApiResponse.response(1, "Current user", user.toDto());
        } else {
            return AllApiResponse.response(403, 0, "Forbidden");
        }
    }

    @PreAuthorize("hasAuthority('get_current_user')")
    @PostMapping("/set-password")
    public HttpEntity<?> getSetPassword(@CurrentUser User user, @RequestBody SetPasswordRequest request) {
        if (user != null) {
            request.setUserId(user.getId());
            return userService.setPassword(request);
        } else {
            return AllApiResponse.response(403, 0, "Forbidden");
        }
    }

    @PreAuthorize("hasAuthority('get_user_filed_attempts')")
    @GetMapping("/get-user-filed-attempts")
    public HttpEntity<?> getFailedAttempts() {
        return bruteForceService.getUserAttempts();
    }

    @PreAuthorize("hasAuthority('remove_user_filed_attempts')")
    @DeleteMapping("/remove-user-filed-attempts")
    public HttpEntity<?> removeFailedAttempts(@RequestParam String ip) {
        return bruteForceService.removeAttemptByIp(ip);
    }
}
