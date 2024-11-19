package com.example.taqsit.service;

import com.example.taqsit.dto.UserDto;
import com.example.taqsit.entity.Role;
import com.example.taqsit.entity.User;
import com.example.taqsit.payload.AllApiResponse;
import com.example.taqsit.payload.LoginRequest;
import com.example.taqsit.payload.SetPasswordRequest;
import com.example.taqsit.repository.RoleRepository;
import com.example.taqsit.repository.UserRepository;
import com.example.taqsit.secret.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final BruteForceService bruteForceService;

    private final RoleRepository roleRepository;

    public HttpEntity<?> createOrEdit(UserDto dto) {
        try {
            Map<String, Object> valid = dto.valid();
            if (!valid.isEmpty()) return AllApiResponse.response(422, 0, "Validator errors!", valid);
            if (dto.getId() != null) {
                User orElse = userRepository.findByUsernameAndDeletedFalse(dto.getUsername()).orElse(null);
                if (orElse != null && !orElse.getId().equals(dto.getId())) {
                    return AllApiResponse.response(422, 0, "This username already exists!");
                }
            } else if (userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())) {
                return AllApiResponse.response(422, 0, "This username already exists!");
            }
            User user;
            if (dto.getId() != null) {
                user = userRepository.findByIdAndDeletedFalse(dto.getId()).orElse(null);
                if (user == null) {
                    return AllApiResponse.response(404, 0, "User not found by id!");
                }
            } else {
                user = new User();
            }
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoles()));
            if (roles.isEmpty()) return AllApiResponse.response(404, 0, "Roles not found!");
            user = dto.dtoToEntity(user);
            user.setRoles(roles);
            if (dto.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
            return AllApiResponse.response(1, String.format("User successfully %s!", dto.getId() == null ? "created" : "updated"));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, String.format("Error %s user", dto.getId() == null ? "create" : "update"));
        }
    }

    public HttpEntity<?> register(UserDto dto) {
        try {
            Map<String, Object> valid = dto.validRegister();
            if (!valid.isEmpty()) return AllApiResponse.response(422, 0, "Validator errors!", valid);
            if (dto.getId() != null) {
                User orElse = userRepository.findByUsernameAndDeletedFalse(dto.getUsername()).orElse(null);
                if (orElse != null && !orElse.getId().equals(dto.getId())) {
                    return AllApiResponse.response(422, 0, "This username already exists!");
                }
            } else if (userRepository.existsByUsernameAndDeletedFalse(dto.getUsername())) {
                return AllApiResponse.response(422, 0, "This username already exists!");
            }
            User user;
            if (dto.getId() != null) {
                user = userRepository.findByIdAndDeletedFalse(dto.getId()).orElse(null);
                if (user == null) {
                    return AllApiResponse.response(404, 0, "User not found by id!");
                }
            } else {
                user = new User();
            }
            Role customer = roleRepository.findByName("customer").orElse(null);
            if (customer == null) return AllApiResponse.response(404, 0, "Role not found!");
            Set<Role> roles = new HashSet<>(Collections.singleton(customer));
            if (roles.isEmpty()) return AllApiResponse.response(404, 0, "Roles not found!");
            user = dto.dtoToEntity(user);
            user.setRoles(roles);
            if (dto.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
            return AllApiResponse.response(1, String.format("User successfully %s!", dto.getId() == null ? "created" : "updated"));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, String.format("Error %s user", dto.getId() == null ? "create" : "update"));
        }
    }

    public HttpEntity<?> getAllUsers(int page, int size,
                                     String search) {
        try {
            List<Integer> depIds = null;
            Page<User> all = userRepository.findAll(
                    where(
                            UserRepository.UserSpecifications.searchByFirstName(search)
                                    .or(UserRepository.UserSpecifications.searchByLastName(search))
                                    .or(UserRepository.UserSpecifications.searchByMiddleName(search))
                                    .or(UserRepository.UserSpecifications.searchByUsername(search))
                    )
                            .and(UserRepository.UserSpecifications.notAdmin()).and(UserRepository.UserSpecifications.deletedFalse()),
                    page == -1 ? Pageable.unpaged() : PageRequest.of(page - 1, size)
            );
            return AllApiResponse.response(1, "All users!", Map.of(
                    "meta", Map.of(
                            "page", page,
                            "size", size,
                            "totalPages", all.getTotalPages(),
                            "totalElements", all.getTotalElements()
                    ),
                    "items", all.stream().map(User::toDto).toList()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error get all users!");
        }
    }

    public HttpEntity<?> getOneUser(Integer userId) {
        try {
            User user = userRepository.findByIdAndDeletedFalse(userId).orElse(null);
            if (user == null) return AllApiResponse.response(404, 0, "User not found by id!");
            return AllApiResponse.response(1, "User by id!", user.toDto());
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error get user by id!");
        }
    }

    public HttpEntity<?> deleteUser(Integer id) {
        try {
            User user = userRepository.findByIdAndDeletedFalse(id).orElse(null);
            if (user == null) return AllApiResponse.response(404, 0, "User not found!");
            user.setDeleted(true);
            userRepository.save(user);
            return AllApiResponse.response(1, "User deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error delete user!");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (bruteForceService.isBlocked()) {
            throw new RuntimeException("attempt_filed");
        }
        try {
            Optional<User> byUsername = userRepository.findByUsernameAndDeletedFalse(username);
            if (byUsername.isPresent()) {
                return byUsername.get();
            } else {
                throw new UsernameNotFoundException("Username or password incorrect!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException("Error enter with login and password!");
        }
    }

    public User loadByUserId(Integer id) {
        try {
            Optional<User> byId = userRepository.findByIdAndDeletedFalse(id);
            return byId.orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> signIn(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User principal = (User) authentication.getPrincipal();
            return AllApiResponse.response(1, "Login successfully", jwtTokenProvider.generateToken(principal));
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BadCredentialsException)
                return AllApiResponse.response(500, 0, e.getMessage(),
                        Map.of(
                                "msg", " Your reaming attempts: " + (bruteForceService.getUserReamingAttempts() + 1),
                                "type", "failed_login",
                                "attempt", bruteForceService.getUserReamingAttempts() + 1
                        )
                );
            return AllApiResponse.response(500, 0, e.getMessage(), e.getMessage().equals("attempt_filed") ? Map.of(
                    "msg", "You have been blocked, please try again in a day",
                    "type", "failed_attempt",
                    "attempt", 0
            ) : null);
        }
    }

    public HttpEntity<?> setPassword(SetPasswordRequest setPasswordRequest) {
        try {
            User user = userRepository.findByIdAndDeletedFalse(setPasswordRequest.getUserId()).orElse(null);
            if (user == null) return AllApiResponse.response(404, 0, "User not found!");
            if (passwordEncoder.matches(setPasswordRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(setPasswordRequest.getNewPassword()));
                user.setLastChangedPasswordDate(new Date());
                userRepository.save(user);
                return AllApiResponse.response(1, "Password saved successfully!");
            } else return AllApiResponse.response(422, 0, "Old password wrong!");
        } catch (Exception e) {
            e.printStackTrace();
            return AllApiResponse.response(500, 0, "Error set password!");
        }
    }

    public User currentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            if (principal != null && !Objects.equals(principal, "anonymousUser")) {
                return (User) principal;
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
