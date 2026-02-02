package com.banking.accountservice.models;

import com.banking.accountservice.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_details", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id", name = "uk_acc_did")
})
public class AccountDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency  currency;

    private BigDecimal dailyWithdrawalLimit;
    private BigDecimal perTransactionLimit;
    private String nomineeName;
    private String nomineeEmail;
    private String nomineeRelationship;
    private Boolean isKycVerified;
}
