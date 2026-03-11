package com.banking.transactionservice.entities;

import com.banking.transactionservice.enums.TransactionStatus;
import com.banking.transactionservice.enums.TransactionType;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "idx_txn_from_account_and_createdAt", columnList = "fromAccountNumber, createdAt"),
        @Index(name = "idx_txn_to_account_and_createdAt", columnList = "toAccountNumber, createdAt")
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String fromAccountNumber;
    private String toAccountNumber;
    private Long fromUserId;
    private Long toUserId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

}
