package com.banking.accountservice.services;

import com.banking.accountservice.dtos.mirror.transaction.DepositRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.TransferRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.WithdrawRequestDto;

public interface BalanceServices {

    String withdraw(WithdrawRequestDto withdrawRequestDto);

    String deposit(DepositRequestDto depositRequestDto);

    String transfer(TransferRequestDto transferRequestDto);
}
