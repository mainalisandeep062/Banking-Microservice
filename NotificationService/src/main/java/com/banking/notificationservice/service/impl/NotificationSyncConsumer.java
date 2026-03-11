package com.banking.notificationservice.service.impl;

import com.banking.notificationservice.dto.external.AccountSyncNotificationDto;
import com.banking.notificationservice.dto.external.TransactionNotificationDto;
import com.banking.notificationservice.dto.external.UserSyncNotificationDTO;
import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationUser;
import com.banking.notificationservice.enums.NotificationType;
import com.banking.notificationservice.enums.TransactionType;
import com.banking.notificationservice.repo.NotificationRepo;
import com.banking.notificationservice.repo.NotificationUserRepo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSyncConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepo notificationRepo;
    private final NotificationUserRepo userRepo;

    private static final String ACCOUNT_CREATION_MESSAGE = "account.creation.message";
    private static final String ACCOUNT_CLOSED_MESSAGE = "account.closed.message";

    @RabbitListener(queues = "user.sync.queue")
    public void consumeUserSync(UserSyncNotificationDTO dto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        log.info("Syncing new user to Notification DB: {}", dto.getEmail());
        NotificationUser recipient = new NotificationUser();

       try {
            if (!userRepo.existsByEmail(dto.getEmail())) {
                recipient.setUserId(dto.getUserId());
                recipient.setEmail(dto.getEmail());
                recipient.setFullName(dto.getFullName());

                userRepo.save(recipient);

                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("Welcome!! You're successfully registered!")
                        .message("Welcome to this banking system. You've successfully registered! You are suggested to open an account and for that first read the manual that will be provided to you soon. Thank you!!")
                        .type(NotificationType.USER_REGISTERED)
                        .recipient(recipient)
                        .reference("UserID: " + recipient.getUserId())
                        .build());

                simpMessagingTemplate.convertAndSendToUser(
                        recipient.getEmail(),
                        "/queue/notification",
                        dto.getEmail());
                log.info("Successfully synced new user: {}", dto.getEmail());
            } else {
                log.info("User with email {} already registered", dto.getEmail());
            }

           //Tell RabbitMQ to delete the message from the queue
           channel.basicAck(tag, false);
        }catch(Exception e){
           log.error("Error processing the User Syncing. Message stays in queue. {}", e.getMessage());
           channel.basicNack(tag, false, true);
       }
    }

    @RabbitListener(queues = "account.sync.queue")
    public void consumeUserSync(AccountSyncNotificationDto dto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Processing new Account creation Notification!!");

        try {
            NotificationUser recipient = userRepo.findByUserId(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            if(dto.getNotificationType().equals("ACCOUNT_CREATED"))
                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("Congratulation!! Your account has been successfully created!")
                        .message("Congratulations!!! your Account has been successfully created.")
                        .type(NotificationType.ACCOUNT_CREATED)
                        .reference("Account number: " + dto.getAccountNumber())
                        .recipient(recipient)
                        .build());
            if(dto.getNotificationType().equals("ACCOUNT_CLOSED"))
                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("Your account has been closed!")
                        .message(ACCOUNT_CLOSED_MESSAGE)
                        .type(NotificationType.ACCOUNT_CLOSED)
                        .reference("Account number: " + dto.getAccountNumber())
                        .recipient(recipient)
                        .build());

            simpMessagingTemplate.convertAndSendToUser(
                        recipient.getEmail(),
                        "/queue/notification",
                        dto);
                log.info("Successfully sent account created/closed message: {}", recipient.getEmail());

            //Tell RabbitMQ to delete the message from the queue
            channel.basicAck(tag, false);
        }catch(Exception e){
            log.error("Error processing notification. Message stays in queue. {}", e.getMessage());
            channel.basicNack(tag, false, true);
        }

    }

    @RabbitListener(queues = "transaction.sync.queue")
    public void consumeTransactionSync(TransactionNotificationDto dto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Processing new Transaction Notification. From: {}", dto);
         try{
            if (dto.getTransactionType().equals(TransactionType.TRANSFER)) {
                NotificationUser receiver = userRepo.findByUserId(dto.getToUserId())
                        .orElseThrow(() -> new RuntimeException("User not found!"));
                NotificationUser sender = userRepo.findByUserId(dto.getFromUserId())
                        .orElseThrow(() -> new RuntimeException("User not found!"));

                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("The transfer was successful!!")
                        .message(dto.getAmount() + " was transferred to " + dto.getToAccountNumber() + " successfully! ")
                        .type(NotificationType.TRANSFER_SENT)
                        .reference("Account number: " + dto.getFromAccountNumber())
                        .recipient(sender)
                        .build());
                simpMessagingTemplate.convertAndSendToUser(
                        sender.getEmail(),
                        "/queue/notification",
                        dto);

                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("The transfer was successful!!")
                        .message(dto.getAmount() + " was transferred From " + dto.getFromAccountNumber() + " to your account successfully! ")
                        .type(NotificationType.TRANSFER_RECEIVED)
                        .reference("Account number: " + dto.getToAccountNumber())
                        .recipient(receiver)
                        .build());
                simpMessagingTemplate.convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/notification",
                        dto);
            }

            if (dto.getTransactionType().equals(TransactionType.CREDIT)) {
                NotificationUser user = userRepo.findByUserId(dto.getToUserId())
                        .orElseThrow(() -> new RuntimeException("User not found!"));
                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("The money was successfully deposited!!")
                        .message(dto.getAmount() + " was Deposited to Account: " + dto.getToAccountNumber() + " successfully! ")
                        .type(NotificationType.CREDIT)
                        .reference("Account number: " + dto.getToAccountNumber())
                        .recipient(user)
                        .build());
                simpMessagingTemplate.convertAndSendToUser(
                        user.getEmail(),
                        "/queue/notification",
                        dto);
            }

            if (dto.getTransactionType().equals(TransactionType.DEBIT)) {
                NotificationUser user = userRepo.findByUserId(dto.getFromUserId())
                        .orElseThrow(() -> new RuntimeException("User not found!"));

                notificationRepo.save(Notification.builder()
                        .read(false)
                        .title("The money was successfully Withdrawn!!")
                        .message(dto.getAmount() + " was Withdrawn from Account: " + dto.getFromAccountNumber() + " successfully! ")
                        .type(NotificationType.DEBIT)
                        .reference("Account number: " + dto.getFromAccountNumber())
                        .recipient(user)
                        .build());
                simpMessagingTemplate.convertAndSendToUser(
                        user.getEmail(),
                        "/queue/notification",
                        dto);

            }
             channel.basicAck(tag, false);
         }catch(Exception e){
             log.error("Error processing Transaction notification. Message stays in queue. {}", e.getMessage());
             channel.basicNack(tag, false, true);
         }
    }
}
