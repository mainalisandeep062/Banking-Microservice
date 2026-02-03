package com.banking.accountservice.config;

import lombok.Builder;

@Builder
public record CurrentUser(
        String subject,
        String userId,
        String role,
        String firstName,
        String lastName
) {}