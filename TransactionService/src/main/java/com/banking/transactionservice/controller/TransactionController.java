package com.banking.transactionservice.controller;

import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.external.HistoryResponseDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;
import com.banking.transactionservice.exception.ApiResponse;
import com.banking.transactionservice.services.TransactionServices;
import com.banking.transactionservice.services.impl.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServices transactionService;
    private final HistoryService historyService;

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

    @GetMapping("/history")
    public ApiResponse<HistoryResponseDto> getTransactionHistory(@RequestParam(defaultValue = "0") int pageNumber,
                                                                 @RequestParam String accountNumber){
        return ApiResponse.success(200, "OK",
                historyService.getHistoryByAccountNumber(accountNumber, pageNumber));
    }

    @GetMapping("/{transactionId}")
    public ApiResponse<TransactionResponseDto> getTransactionById(@PathVariable Long transactionId) {
        return ApiResponse.success(200, "OK", transactionService.getTransactionById(transactionId));
    }

}
