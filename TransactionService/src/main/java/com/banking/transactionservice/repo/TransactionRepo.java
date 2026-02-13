package com.banking.transactionservice.repo;

import com.banking.transactionservice.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("""
                SELECT t from Transaction t
                WHERE t.toAccountNumber = :accountNumber
                OR t.fromAccountNumber = :accountNumber
                ORDER BY t.createdAt DESC
            """)
    Page<Transaction> findTransactionByAccount(@Param("accountNumber") String accountNumber, Pageable pageable);
}
