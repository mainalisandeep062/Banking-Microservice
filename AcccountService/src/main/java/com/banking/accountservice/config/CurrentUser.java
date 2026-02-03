package com.banking.accountservice.config;

import lombok.Builder;

@Builder
public record CurrentUser(
        String subject,
        Integer userId,
        String role,
        String firstName,
        String lastName
) {}