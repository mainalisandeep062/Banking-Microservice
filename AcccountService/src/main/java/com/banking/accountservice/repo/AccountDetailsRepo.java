package com.banking.accountservice.repo;

import com.banking.accountservice.models.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepo extends JpaRepository<AccountDetails, Long> {

}
