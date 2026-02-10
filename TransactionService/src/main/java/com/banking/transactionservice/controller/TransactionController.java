package com.banking.transactionservice.controller;

import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;
import com.banking.transactionservice.exception.ApiResponse;
import com.banking.transactionservice.services.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServices transactionService;

    @PutMapping("/withdraw")
    public ApiResponse<TransactionResponseDto> withdraw(@RequestBody WithdrawRequestDto withdrawRequestDto){
        return ApiResponse.success(200, "OK", transactionService.withdraw(withdrawRequestDto));
    }

    @PutMapping("/deposit")
    public ApiResponse<TransactionResponseDto> deposit(@RequestBody DepositRequestDto depositRequestDto){
        return ApiResponse.success(200, "OK", transactionService.deposit(depositRequestDto));
    }

    @PutMapping("/transfer")
    public ApiResponse<TransactionResponseDto> withdraw(@RequestBody TransferRequestDto transferRequestDto){
        return ApiResponse.success(200, "OK", transactionService.transfer(transferRequestDto));
    }

}
