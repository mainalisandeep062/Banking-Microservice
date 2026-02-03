package com.banking.userservices.Controllers;

import com.banking.userservices.Config.CustomUserDetails;
import com.banking.userservices.Config.JwtConfig;
import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.AuthRequestDto;
import com.banking.userservices.dto.AuthResponseDto;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.exception.ApiResponse;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserServices userServices;
    private final JwtConfig jwt;

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto> login(
            @RequestBody AuthRequestDto authRequestDto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequestDto.getEmail(),
                                authRequestDto.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        int updated = userRepo.updateUserLastLogin(user.getUserId());
        if (updated != 1) {
            log.error("Failed to update last_login for userId={}", user.getUserId());
        }

        String token = jwt.generateToken(userDetails);

        return ApiResponse.success(
                200,
                "ok",
                AuthResponseDto.builder()
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .role(user.getRole())
                        .token(token)
                        .build()
        );
    }


    @PostMapping("/register")
    public ApiResponse<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto){
        return ApiResponse.success(200, "ok", userServices.registerUser(userRequestDto));
    }

    @PostMapping("/authenticate")
    public ApiResponse<Boolean> authenticate(@RequestParam String  email,
                                             @RequestParam String password) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(user.getPassword(), password))
            throw new RuntimeException("Invalid password");
        return ApiResponse.success(200, "ok", true);
}


}
