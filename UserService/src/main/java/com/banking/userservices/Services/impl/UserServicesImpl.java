package com.banking.userservices.Services.impl;

import com.banking.userservices.Models.User;
import com.banking.userservices.Repo.UserRepo;
import com.banking.userservices.Services.UserServices;
import com.banking.userservices.dto.UserSyncNotificationDTO;
import com.banking.userservices.dto.user.UserRequestDto;
import com.banking.userservices.dto.user.UserResponseDto;
import com.banking.userservices.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServicesImpl implements UserServices {

    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final UserRepo repo;

    @Override
    public UserResponseDto registerUser(UserRequestDto userRequest) {
        if(repo.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("User With this email already exists!!!");
        }
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .dateOfBirth(userRequest.getDateOfBirth())
                .role(userRequest.getRole())
                .isActive(true)
                .build();
        repo.save(user);

        try{
            UserSyncNotificationDTO syncData = UserSyncNotificationDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .build();

            rabbitTemplate.convertAndSend(
                    "banking.direct.exchange",
                    "user.sync.key",
                    syncData
            );
        }catch (Exception ex){
            log.error("Failed to send user sync message: {}", ex.getMessage());
        }
        return toDto(user);
    }

    @Override
    public UserResponseDto fetchMyProfile(String email) {
        return toDto(repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found!")));
    }

    @Override
    public UserResponseDto updateMyProfile(String email, UserUpdateDto userRequest) {
        if(userRequest == null)
            return null;
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found!"));
        if (userRequest.getFirstName() != null) user.setFirstName(userRequest.getFirstName());
        if (userRequest.getLastName() != null) user.setLastName(userRequest.getLastName());
        if (userRequest.getDateOfBirth() != null) user.setDateOfBirth(userRequest.getDateOfBirth());

        //todo: add verification step to update the email
        if (userRequest.getEmail() != null) user.setEmail(userRequest.getEmail());

        repo.save(user);
        return toDto(user);
    }

    @Override
    public String updatePassword(String email, String oldPassword, String newPassword) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found!"));
        if(passwordEncoder.matches(oldPassword,user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
        }else{
            throw new RuntimeException("Old password doesn't match!");
        }
        repo.save(user);
        return "Password updated successfully!";
    }

    @Override
    public UserResponseDto toDto (User user){
        if(user == null){
            return null;
        }

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFirstName() +  " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public Boolean checkIfUserExists(Long userId) {
       return repo.existsByUserId(userId);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        return toDto(repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not found!!")));
    }
}

