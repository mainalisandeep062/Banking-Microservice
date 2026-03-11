package com.banking.userservices.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}

