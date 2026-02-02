package com.banking.accountservice.repo;

import com.banking.accountservice.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(Long accountId);
    Optional<Account> findByAccountNumber(String accountNumber);
    Boolean  existsByAccountNumber(String accountNumber);
}
