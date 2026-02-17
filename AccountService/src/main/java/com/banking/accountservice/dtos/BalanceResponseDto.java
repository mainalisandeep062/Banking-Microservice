package com.banking.accountservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal currentBalance;

}
