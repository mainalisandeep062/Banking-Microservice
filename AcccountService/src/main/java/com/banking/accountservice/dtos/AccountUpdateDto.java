package com.banking.accountservice.dtos;

import com.banking.accountservice.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountUpdateDto {
    private Status status;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal perTransactionLimit;
    private Boolean isKycVerified;

}
