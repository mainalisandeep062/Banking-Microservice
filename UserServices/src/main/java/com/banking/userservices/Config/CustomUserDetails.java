package com.banking.userservices.Config;

import com.banking.userservices.Models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public User getUser() {
        return user;
    }

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName(){
        return user.getLastName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // email is username
    }

}

