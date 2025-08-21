package com.example.portfolioappprog5resit.config.security;

import com.example.portfolioappprog5resit.domain.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Integer userId;

    public CustomUserDetails(AppUser u, Collection<? extends GrantedAuthority> auth) {
        super(u.getUsername(), u.getPasswordHash(), auth);
        this.userId = u.getId();
    }

    public Integer getUserId() { return userId; }
}
