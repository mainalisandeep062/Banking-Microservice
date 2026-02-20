package com.banking.notificationservice.dto.external;

import com.banking.notificationservice.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSyncNotificationDto {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private AccountType accountType;
    private String notificationType;
}
