package com.banking.userservices.Controllers;

import com.banking.userservices.Config.JwtConfig;
import com.banking.userservices.Converter.UserConverter;
import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.AuthRequestDto;
import com.banking.userservices.dto.AuthResponseDto;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserServices userServices;
    private final JwtConfig jwt;
    private final UserConverter userConverter;

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto){
        User user =  userRepo.findByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if(!passwordEncoder.matches(authRequestDto.getPassword(),user.getPassword())){
            throw new BadCredentialsException("Wrong email or password");
        }

        String role = user.getRole().toString();
        String token = jwt.generateToken(authRequestDto.getEmail(), role);

        return ApiResponse.success(200,
                "ok",
                new AuthResponseDto().builder()
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .token(token)
                .build());
    }

    @PostMapping("/register")
    public ApiResponse<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto){
        return ApiResponse.success(200, "ok", userServices.registerUser(userRequestDto));
    }


}
