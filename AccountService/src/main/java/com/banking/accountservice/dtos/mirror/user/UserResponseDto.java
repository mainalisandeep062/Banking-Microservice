package com.banking.accountservice.dtos.mirror.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String fullName;
    private Role role;
    private Boolean isActive;
    private LocalDate createdAt;
}

enum Role {
    ADMIN,
    USER
}
