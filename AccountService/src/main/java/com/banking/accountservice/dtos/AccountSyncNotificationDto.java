package com.banking.accountservice.dtos;

import com.banking.accountservice.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSyncNotificationDto implements Serializable {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private AccountType accountType;
    private String notificationType;
}
