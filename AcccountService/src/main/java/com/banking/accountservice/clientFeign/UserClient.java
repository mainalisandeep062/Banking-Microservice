package com.banking.accountservice.clientFeign;

import com.banking.accountservice.exception.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/user")
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/exists-by/{userId}")
    ApiResponse<Boolean> checkIfUserExists(@PathVariable Long userId);

}
