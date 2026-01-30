package com.banking.acccountservice.repo;

import com.banking.acccountservice.models.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepo extends JpaRepository<AccountDetails, Long> {

}
