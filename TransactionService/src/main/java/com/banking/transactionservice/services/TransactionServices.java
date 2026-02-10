package com.banking.transactionservice.services;

import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;

public interface TransactionServices {
    TransactionResponseDto withdraw(WithdrawRequestDto withdrawRequestDto);

    TransactionResponseDto deposit(DepositRequestDto depositRequestDto);

    TransactionResponseDto transfer(TransferRequestDto transferRequestDto);
}
