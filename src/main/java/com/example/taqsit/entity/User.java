package com.example.taqsit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@Entity(name = "users")
@NoArgsConstructor
@Builder

public class User extends BaseEntity implements UserDetails, Serializable {
    private String lastName;

    private String firstName;

    private String middleName;

    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastChangedPasswordDate;
    private Integer photo;
    @JsonIgnore
    private boolean deleted = false;

    public User(String lastName, String firstName, String middleName, String username, String password, Set<Role> roles) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role r : roles) {
            for (Permission permission : r.getPermission()) {
                authorities.add((GrantedAuthority) permission::getName);
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Map<String, Object> toDto() {
        return new HashMap<>() {{
            put("id", getId());
            put("firstName", firstName);
            put("lastName", lastName);
            put("middleName", middleName);
            put("username", username);
            put("requiredChangePassword", lastChangedPasswordDate == null || (((new Date().getTime() - lastChangedPasswordDate.getTime()) - 60L * 86400 * 1000) > 0));
            put("roles", roles != null ? roles.stream().map(Role::toDtoListPerm).toList() : "");
        }};
    }
}
