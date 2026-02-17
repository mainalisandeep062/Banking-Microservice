package com.banking.accountservice.dtos;

import com.banking.accountservice.enums.AccountType;
import com.banking.accountservice.enums.Currency;
import com.banking.accountservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
