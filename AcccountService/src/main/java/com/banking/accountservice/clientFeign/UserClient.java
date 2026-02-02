package com.banking.accountservice.clientFeign;

import com.banking.accountservice.dtos.external.UserResponseDto;
import com.banking.accountservice.exception.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("api/user/exists-by/{userId}")
    ApiResponse<Boolean> checkIfUserExists(@PathVariable Long userId);

    @GetMapping("/api/user")
    ApiResponse<UserResponseDto> getUserById(@RequestParam Long userId);
}
