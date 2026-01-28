package com.banking.userservices.Services.impl;

import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserServices {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo repo;

    @Override
    public UserResponseDto registerUser(UserRequestDto userRequest) {
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .dateOfBirth(userRequest.getDateOfBirth())
                .role(userRequest.getRole())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        repo.save(user);
        return toDto(user);
    }

    @Override
    public UserResponseDto fetchMyProfile(String email) {
        return toDto(repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found!")));
    }

    @Override
    public UserResponseDto updateMyProfile(UserRequestDto userRequest) {
        return null;
    }

    @Override
    public UserResponseDto toDto (User user){
        if(user == null){
            return null;
        }

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFirstName() +  " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

