package com.banking.acccountservice.dtos;

import com.banking.acccountservice.enums.AccountType;
import com.banking.acccountservice.enums.Currency;
import com.banking.acccountservice.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private AccountType accountType;
    private Status status;
    private LocalDateTime createdDate;
    private Currency currency;
    private Boolean isKycVerified;
}
