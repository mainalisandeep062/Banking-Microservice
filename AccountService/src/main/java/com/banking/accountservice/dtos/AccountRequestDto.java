package com.banking.accountservice.dtos;

import com.banking.accountservice.enums.AccountType;
import com.banking.accountservice.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AccountRequestDto {
    private Long userId;
    private AccountType accountType;
    private LocalDate maturityDate;
    private Currency currency;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal perTransactionLimit;
    private String nomineeName;
    private String nomineeEmail;
    private String nomineeRelationship;
}
