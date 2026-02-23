package com.banking.notificationservice.dto.external;

import com.banking.notificationservice.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionNotificationDto {
    private Long transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Long  fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private TransactionType transactionType;
}
