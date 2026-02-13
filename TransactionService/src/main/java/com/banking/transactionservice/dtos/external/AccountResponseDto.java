package com.banking.transactionservice.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private AccountType accountType;
    private Status status;
    private LocalDate createdDate;
    private Currency currency;
    private Boolean isKycVerified;
}
enum AccountType {
    SAVING,
    CURRENT,
    FIXED_DEPOSIT
}

enum Status {
    ACTIVE,
    CLOSED,
    BLOCKED
}

enum Currency {
    NPR,
    EUR,
    USD,
    INR
}


