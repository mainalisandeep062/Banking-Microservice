package com.banking.accountservice.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriticalResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal currentBalance;

}
