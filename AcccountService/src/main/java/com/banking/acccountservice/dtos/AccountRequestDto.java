package com.banking.acccountservice.dtos;

import com.banking.acccountservice.enums.AccountType;
import com.banking.acccountservice.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {
    private Long userId;
    private AccountType accountType;
    private String maturityDate;
    private Currency currency;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal perTransactionLimit;
    private String nomineeName;
    private String nomineeEmail;
    private String nomineeRelationship;
}
