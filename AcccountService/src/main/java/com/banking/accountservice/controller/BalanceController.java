package com.banking.accountservice.controller;

import com.banking.accountservice.dtos.mirror.transaction.DepositRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.TransactionResponseDto;
import com.banking.accountservice.dtos.mirror.transaction.TransferRequestDto;
import com.banking.accountservice.dtos.mirror.transaction.WithdrawRequestDto;
import com.banking.accountservice.exception.ApiResponse;
import com.banking.accountservice.services.BalanceServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/transaction")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceServices balanceServices;

    @PutMapping("/withdraw")
    public ApiResponse<TransactionResponseDto> withdraw(@RequestBody WithdrawRequestDto withdrawRequestDto) {
        return ApiResponse.success(200, "OK", balanceServices.withdraw(withdrawRequestDto));
    }

    @PutMapping("/deposit")
    public ApiResponse<TransactionResponseDto> deposit(@RequestBody DepositRequestDto depositRequestDto) {
        return ApiResponse.success(200, "OK", balanceServices.deposit(depositRequestDto));
    }

    @PutMapping("/transfer")
    public  ApiResponse<TransactionResponseDto> transfer(@RequestBody TransferRequestDto transferRequestDto) {
        return ApiResponse.success(200, "OK", balanceServices.transfer(transferRequestDto));
    }
}
