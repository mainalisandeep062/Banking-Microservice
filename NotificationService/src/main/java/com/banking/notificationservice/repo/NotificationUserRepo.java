package com.banking.notificationservice.repo;

import com.banking.notificationservice.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationUserRepo extends JpaRepository<NotificationUser, String> {

    boolean existsByEmail(String email);
}
