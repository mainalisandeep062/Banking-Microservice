package com.banking.acccountservice.config;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGeneration {
    public String generateAccountNumber() {
        return String.valueOf((long) (Math.random() * 1_000_000_000_000L));
    }
}
