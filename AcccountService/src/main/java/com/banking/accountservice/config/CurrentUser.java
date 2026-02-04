package com.banking.accountservice.config;

import lombok.Builder;


@Builder
public record CurrentUser(
        String subject,
        Long userId,
        String role,
        String firstName,
        String lastName
) {}