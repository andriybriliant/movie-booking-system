package com.andriibryliant.movies.auth.config;

import com.andriibryliant.movies.auth.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final String name;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(UserCredential userCredential) {
        this.name = userCredential.getEmail();
        this.password = userCredential.getPassword();
        
        if (userCredential.getRole() != null && !userCredential.getRole().isEmpty()) {
            this.authorities = Arrays.stream(userCredential.getRole().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

}
