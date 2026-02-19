package com.banking.userservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSyncNotificationDTO implements Serializable {
    private Long userId;
    private String email;
    private String fullName;
}