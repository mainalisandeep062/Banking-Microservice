package com.banking.accountservice.services;

import com.banking.accountservice.dtos.mirror.transaction.DepositRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.TransactionResponseDto;
import com.banking.accountservice.dtos.mirror.transaction.TransferRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.WithdrawRequestDto;

public interface BalanceServices {

    TransactionResponseDto withdraw(WithdrawRequestDto withdrawRequestDto);

    TransactionResponseDto deposit(DepositRequestDto depositRequestDto);

    TransactionResponseDto transfer(TransferRequestDto transferRequestDto);
}
