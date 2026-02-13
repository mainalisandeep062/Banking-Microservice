package com.banking.transactionservice.dtos.external;

import com.banking.transactionservice.dtos.TransactionResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class HistoryResponseDto {
    private String accountNumber;
    private String accountHolderName;
    private LocalDate createdDate;
    private List<TransactionResponseDto>  transactions;
}
