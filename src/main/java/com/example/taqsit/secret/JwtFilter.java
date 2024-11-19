package com.example.taqsit.secret;

import com.example.taqsit.repository.UserRepository;
import com.example.taqsit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService authService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        UserDetails userDetails = getUserDetails(httpServletRequest);
        if (userDetails != null) {
            if (userDetails.isEnabled()
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public UserDetails getUserDetails(HttpServletRequest httpServletRequest) {
        try {
            String tokenClient = httpServletRequest.getHeader("Authorization");
            if (tokenClient != null) {
                if (tokenClient.startsWith("Bearer ")) {
                    tokenClient = tokenClient.substring(7);
                    if (jwtTokenProvider.validateToken(tokenClient)) {
                        String userIdFromToken = jwtTokenProvider.getUserIdFromToken(tokenClient);
                        return authService.loadByUserId(Integer.valueOf(userIdFromToken));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
