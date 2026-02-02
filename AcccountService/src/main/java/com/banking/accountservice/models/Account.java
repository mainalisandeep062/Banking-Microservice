package com.banking.accountservice.models;

import com.banking.accountservice.enums.AccountType;
import com.banking.accountservice.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate maturityDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private AccountDetails accountDetails;
}
