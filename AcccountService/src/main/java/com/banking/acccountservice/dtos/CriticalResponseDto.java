package com.banking.acccountservice.dtos;

import com.banking.acccountservice.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriticalResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal currentBalance;

}
