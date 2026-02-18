package com.banking.notificationservice.configs;

import lombok.Builder;


@Builder
public record CurrentUser(
        String subject,
        Long userId,
        String role,
        String firstName,
        String lastName
) {}