package com.banking.transactionservice.clientFeign;

import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.external.AccountResponseDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;
import com.banking.transactionservice.exception.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @PutMapping("/api/account/transaction/withdraw")
    ApiResponse<TransactionResponseDto> withdraw(@RequestBody WithdrawRequestDto withdrawRequestDto);

    @PutMapping("/api/account/transaction/deposit")
    ApiResponse<TransactionResponseDto> deposit(@RequestBody DepositRequestDto depositRequestDto);

    @PutMapping("/api/account/transaction/transfer")
    ApiResponse<TransactionResponseDto> transfer(@RequestBody TransferRequestDto transferRequestDto);

    @GetMapping("/api/account/account-number/{accountNumber}")
    ApiResponse<AccountResponseDto> getAccountByAccountNumber(@PathVariable String accountNumber);
}
