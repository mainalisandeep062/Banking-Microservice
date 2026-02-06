package com.banking.accountservice.repo;

import com.banking.accountservice.models.ProcessedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedTransactionRepo extends JpaRepository<ProcessedTransaction, Long> {
    boolean existsByProcessedTransactionId(String processedTransactionId);
}
