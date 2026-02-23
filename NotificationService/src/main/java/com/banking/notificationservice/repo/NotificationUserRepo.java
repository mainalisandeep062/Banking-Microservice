package com.banking.notificationservice.repo;

import com.banking.notificationservice.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationUserRepo extends JpaRepository<NotificationUser, String> {

    boolean existsByEmail(String email);

    Optional<NotificationUser> findByUserId(Long userId);

}
