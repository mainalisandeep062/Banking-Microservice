package com.banking.userservices.Controllers;

import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;

    @GetMapping("/profile")
    public ApiResponse<UserResponseDto> fetchMyProfile(@AuthenticationPrincipal String email) {
        return ApiResponse.success(200, "ok", userServices.fetchMyProfile(email));
    }
}
