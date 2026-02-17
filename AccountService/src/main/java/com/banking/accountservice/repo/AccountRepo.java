package com.banking.accountservice.repo;

import com.banking.accountservice.models.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    @Query("""
                    SELECT a FROM Account a
                    WHERE a.accountNumber = :accountNumber
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);

    Boolean existsByAccountNumber(String accountNumber);

    @Query(value = """
                    SELECT a.* FROM account a
                    WHERE a.user_id = :userId
            """, nativeQuery = true)
    List<Account> findByUserId(@Param("userId") Long userId);
}
