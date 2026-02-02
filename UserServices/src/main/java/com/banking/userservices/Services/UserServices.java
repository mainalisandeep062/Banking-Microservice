package com.banking.userservices.Services;

import com.banking.userservices.Models.User;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.dto.user.UserUpdateDto;

public interface UserServices {
    UserResponseDto registerUser(UserRequestDto userRequest);

    UserResponseDto fetchMyProfile(String email);

    UserResponseDto updateMyProfile(String email, UserUpdateDto userRequest);

    String updatePassword(String email, String oldPassword, String newPassword);

    UserResponseDto toDto(User user);

    Boolean checkIfUserExists(Long userId);
}
