package com.banking.userservices.Converter;

import com.banking.userservices.Models.User;
import org.springframework.stereotype.Component;
import com.banking.userservices.dto.user.UserResponseDto;

@Component
public class UserConverter {

    public UserResponseDto toDto (User user){
        if(user == null){
            return null;
        }
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFirstName() +  " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();

        return userResponseDto;

    }
}
