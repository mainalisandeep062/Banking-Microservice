package com.banking.userservices.Controllers;

import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.dto.user.UserUpdateDto;
import com.banking.userservices.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @GetMapping("/profile")
    public ApiResponse<UserResponseDto> fetchMyProfile(@AuthenticationPrincipal String email) {
        return ApiResponse.success(200, "ok", userServices.fetchMyProfile(email));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDto> getUserById(@PathVariable Long userId) {
        return ApiResponse.success(200, "OK", userServices.getUserById(userId));
    }

    @PutMapping("/update-profile")
    public ApiResponse<UserResponseDto> updateMyProfile(@AuthenticationPrincipal String email, @RequestBody UserUpdateDto userUpdateDto ) {
        return ApiResponse.success(200, "ok", userServices.updateMyProfile(email, userUpdateDto));
    }

    @PatchMapping("/change-password")
    public ApiResponse<String>  changePassword(@AuthenticationPrincipal String email, String oldPassword, String newPassword) {
        return ApiResponse.success(200, "ok", userServices.updatePassword(email, oldPassword, newPassword));
    }

    @GetMapping("/exists-by/{userId}")
    public ApiResponse<Boolean> checkIfUserExists(@PathVariable Long userId){
        return ApiResponse.success(200, "OK", userServices.checkIfUserExists(userId));
    }

}
