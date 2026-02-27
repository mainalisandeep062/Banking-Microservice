package com.banking.notificationservice.repo;

import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByRecipientOrderByCreatedAtDesc(NotificationUser recipient);

    List<Notification> findAllByRecipientAndIsReadFalse(NotificationUser recipient);

    long countByRecipientAndIsReadFalse(NotificationUser recipient);

    Optional<Notification> findById(Integer id);

}
