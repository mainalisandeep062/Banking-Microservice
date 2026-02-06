package com.banking.accountservice.models;

import com.banking.accountservice.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_transaction")
public class ProcessedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String processedTransactionId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate date;

}


//Validation first → prevents invalid requests immediately.
//
//Idempotency check → ensures repeated requests don’t double-deposit.
//
//Pessimistic lock → prevents concurrency issues on balance updates.
//
//Account status check → ensures money cannot go into closed/frozen accounts.
//
//Atomic balance + transaction save → @Transactional ensures consistency.
//
//Audit-friendly → every deposit is saved with transaction ID, type, amount, and account reference.
