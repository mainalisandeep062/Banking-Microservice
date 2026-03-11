package com.banking.notificationservice.repo;

import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByRecipientOrderByCreatedAtDesc(NotificationUser recipient);

    List<Notification> findAllByRecipientAndReadFalse(NotificationUser recipient);

    long countByRecipientAndReadFalse(NotificationUser recipient);

    Optional<Notification> findById(Integer id);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient = :user AND n.read = false")
    int markAllAsReadByRecipient(@Param("user") NotificationUser user);

}
