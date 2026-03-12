package com.banking.transactionservice.services.impl;

import com.banking.transactionservice.clientFeign.AccountClient;
import com.banking.transactionservice.dtos.TransactionResponseDto;
import com.banking.transactionservice.dtos.external.TransactionNotificationDto;
import com.banking.transactionservice.dtos.request.DepositRequestDto;
import com.banking.transactionservice.dtos.request.TransferRequestDto;
import com.banking.transactionservice.dtos.request.WithdrawRequestDto;
import com.banking.transactionservice.entities.Transaction;
import com.banking.transactionservice.enums.TransactionStatus;
import com.banking.transactionservice.repo.TransactionRepo;
import com.banking.transactionservice.services.TransactionServices;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServicesImpl implements TransactionServices {

    private final TransactionRepo transactionRepo;
    private final AccountClient accountClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public TransactionResponseDto withdraw(WithdrawRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Empty request. Can't withdraw!");
        }

        Transaction transaction = Transaction.builder()
                .fromAccountNumber(dto.getFromAccountNumber())
                .transactionType(dto.getTransactionType())
                .amount(dto.getAmount())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepo.save(transaction);
        dto.setTransactionId(transaction.getTransactionId());

        try {
            TransactionResponseDto response =
                    accountClient.withdraw(dto).getBody();
            if (response == null) {
                throw new RuntimeException("Account service returned null response");
            }
            transaction.setFromUserId(response.getFromUserId());
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        } catch (Exception ex) {

            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: " + ex.getMessage());
        }

        Transaction savedTransaction = transactionRepo.save(transaction);
        try {
            sendTransactionNotification(savedTransaction);
        }catch (Exception ex) {
            log.error("Failed to send withdraw notification for transaction ID: {}. Error: {}",
                    savedTransaction.getTransactionId(), ex.getMessage());
        }

        return toDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDto deposit(DepositRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Empty request. Can't deposit!");
        }

        Transaction transaction = Transaction.builder()
                .toAccountNumber(dto.getToAccountNumber())
                .transactionType(dto.getTransactionType())
                .amount(dto.getAmount())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepo.save(transaction);
        dto.setTransactionId(transaction.getTransactionId());

        try {
            log.info("Reached the try block");
            TransactionResponseDto response =
                    accountClient.deposit(dto).getBody();

            if (response == null) {
                throw new RuntimeException("Account service returned null response");
            }
            transaction.setToUserId(response.getToUserId());
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        } catch (Exception ex) {

            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: " + ex.getMessage());
        }

        Transaction savedTransaction = transactionRepo.save(transaction);

        try{
            sendTransactionNotification(savedTransaction);
        } catch(Exception ex){
            log.error("Failed to send deposit notification for transaction ID: {}. Error: {}",
                    savedTransaction.getTransactionId(), ex.getMessage());
        }

        return toDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponseDto transfer(TransferRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Empty request. Can't transfer!");
        }

        Transaction transaction = Transaction.builder()
                .fromAccountNumber(dto.getFromAccountNumber())
                .toAccountNumber(dto.getToAccountNumber())
                .transactionType(dto.getTransactionType())
                .amount(dto.getAmount())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepo.save(transaction);
        dto.setTransactionId(transaction.getTransactionId());

        try {
            TransactionResponseDto response =
                    accountClient.transfer(dto).getBody();

            if (response == null) {
                throw new RuntimeException("Account service returned null response");
            }
            transaction.setFromUserId(response.getFromUserId());
            transaction.setToUserId(response.getToUserId());
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());

        } catch (Exception ex) {

            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepo.save(transaction);

            throw new RuntimeException("Transaction Failed: " + ex.getMessage());
        }

        Transaction savedTransaction = transactionRepo.save(transaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getTransactionId());

        try{
            sendTransactionNotification(savedTransaction);
        }catch(Exception ex){
            log.error("Failed to send transfer notification for transaction ID: {}. Error: {}",
                    savedTransaction.getTransactionId(), ex.getMessage());
        }
        log.info("Transaction notification sent for ID: {}", savedTransaction.getTransactionId());

        return toDto(savedTransaction);
    }

    private void sendTransactionNotification(Transaction transaction) {

        TransactionNotificationDto notification =
                TransactionNotificationDto.builder()
                        .transactionId(transaction.getTransactionId())
                        .fromAccountNumber(transaction.getFromAccountNumber())
                        .fromUserId(transaction.getFromUserId())
                        .toAccountNumber(transaction.getToAccountNumber())
                        .toUserId(transaction.getToUserId())
                        .amount(transaction.getAmount())
                        .transactionType(transaction.getTransactionType())
                        .build();

        rabbitTemplate.convertAndSend(
                "banking.direct.exchange",
                "transaction.sync.key",
                notification
        );
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionResponseDto getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction with ID " + transactionId + " not found!"));
        return toDto(transaction);
    }

    private TransactionResponseDto toDto(Transaction txn) {
        return TransactionResponseDto.builder()
                .transactionId(txn.getTransactionId())
                .fromAccountNumber(txn.getFromAccountNumber())
                .fromUserId(txn.getFromUserId())
                .toAccountNumber(txn.getToAccountNumber())
                .toUserId(txn.getToUserId())
                .transactionType(txn.getTransactionType())
                .amount(txn.getAmount())
                .status(txn.getStatus())
                .createdAt(txn.getCreatedAt())
                .completedAt(txn.getCompletedAt())
                .build();
    }
}
