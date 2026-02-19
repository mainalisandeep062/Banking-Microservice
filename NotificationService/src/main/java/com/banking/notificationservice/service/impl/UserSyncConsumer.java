package com.banking.notificationservice.service.impl;

import com.banking.notificationservice.dto.external.UserSyncNotificationDTO;
import com.banking.notificationservice.entity.NotificationUser;
import com.banking.notificationservice.repo.NotificationUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncConsumer {

    private final NotificationUserRepo userRepo;

    @RabbitListener(queues = "user.sync.queue") // Make sure this queue is defined in Notification Service
    public void consumeUserSync(UserSyncNotificationDTO dto) {
        log.info("Syncing new user to Notification DB: {}", dto.getEmail());

        NotificationUser userMapping = new NotificationUser();
        userMapping.setUserId(dto.getUserId());
        userMapping.setEmail(dto.getEmail());
        userMapping.setFullName(dto.getFullName());

        userRepo.save(userMapping);
    }
}