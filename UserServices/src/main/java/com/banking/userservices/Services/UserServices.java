package com.banking.userservices.Services;

import com.banking.userservices.Models.User;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;

public interface UserServices {
    UserResponseDto registerUser(UserRequestDto userRequest);

    UserResponseDto fetchMyProfile(String email);

    UserResponseDto updateMyProfile(UserRequestDto userRequest);

    UserResponseDto toDto (User user);

}
