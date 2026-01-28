package com.banking.userservices.dto.user;

import com.banking.userservices.Enum.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Role role;
}
