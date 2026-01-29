package com.banking.userservices.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;


}
