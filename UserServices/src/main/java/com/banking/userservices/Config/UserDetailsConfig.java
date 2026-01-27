package com.banking.userservices.Config;

import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsConfig {
    private final UserRepo userRepo;

    @Bean
    public String getEmail(@AuthenticationPrincipal String email){
        return email;
    }

    @Bean
    public User CurrentUser(@AuthenticationPrincipal String email){
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
    }

}
