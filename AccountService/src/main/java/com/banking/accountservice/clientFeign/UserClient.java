package com.banking.accountservice.clientFeign;

import com.banking.accountservice.dtos.mirror.user.UserResponseDto;
import com.banking.accountservice.exception.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("api/user/exists-by/{userId}")
    ApiResponse<Boolean> checkIfUserExists(@PathVariable Long userId);

    @GetMapping("/api/user")
    ApiResponse<UserResponseDto> getUserById(@RequestParam Long userId);

    @PostMapping("api/auth/authenticate")
    ApiResponse<Boolean> authenticate(@RequestBody Map<String, String> credentials);
}
