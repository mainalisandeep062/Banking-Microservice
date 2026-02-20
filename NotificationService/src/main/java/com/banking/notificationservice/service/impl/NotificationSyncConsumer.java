package com.banking.notificationservice.service.impl;

import com.banking.notificationservice.dto.external.UserSyncNotificationDTO;
import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationUser;
import com.banking.notificationservice.enums.NotificationType;
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

    private static final String REGISTRATION_MESSAGE = """ 
                                    Welcome to this banking system. You've successfully registered!
                                    You are suggested to open an account and for that first read the manual that will be provided to you soon.
                                    Thank you!!
                                """;

    @RabbitListener(queues = "user.sync.queue")
    public void consumeUserSync(UserSyncNotificationDTO dto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        log.info("Syncing new user to Notification DB: {}", dto.getEmail());
        NotificationUser recipient = new NotificationUser();

       try {
//            if (!userRepo.existsByEmail(recipient.getEmail())) {
                recipient.setUserId(dto.getUserId());
                recipient.setEmail(dto.getEmail());
                recipient.setFullName(dto.getFullName());

                userRepo.save(recipient);

                notificationRepo.save(Notification.builder()
                        .isRead(false)
                        .title("Welcome!! You're successfully registered!")
                        .message(REGISTRATION_MESSAGE)
                        .type(NotificationType.USER_REGISTERED)
                        .recipient(recipient)
                        .build());

                simpMessagingTemplate.convertAndSendToUser(
                        recipient.getEmail(),
                        "/queue/notification",
                        dto.getEmail());
                log.info("Successfully synced new user: {}", dto.getEmail());
//            } else {
//                log.info("User with email {} already registered", dto.getEmail());
//            }

           //Tell RabbitMQ to delete the message from the queue
           channel.basicAck(tag, false);
        }catch(Exception e){
           log.error("Error processing the User Syncing. Message stays in queue. {}", e.getMessage());
           channel.basicNack(tag, false, true);
       }
    }


}