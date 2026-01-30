package com.banking.acccountservice.models;

import com.banking.acccountservice.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_details")
public class AccountDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private Currency  currency;
    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal perTransactionLimit;
    private String nomineeName;
    private String nomineeRelationship;
    private Boolean isKycVerified;
}
