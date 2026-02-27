package com.banking.notificationservice.service.impl;

import com.banking.notificationservice.configs.CurrentUser;
import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.entity.Notification;
import com.banking.notificationservice.entity.NotificationUser;
import com.banking.notificationservice.repo.NotificationRepo;
import com.banking.notificationservice.repo.NotificationUserRepo;
import com.banking.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final NotificationUserRepo userRepo;

    private NotificationUser resolveCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new RuntimeException("Unauthenticated");

        Object principal = auth.getPrincipal();
        if (principal instanceof CurrentUser cu) {
            return userRepo.findByUserId(cu.userId())
                    .orElseThrow(() -> new RuntimeException("Notification user not found"));
        }

        // If principal is email (string) or other, fallback to email lookup
        String email = null;
        if (principal instanceof String) email = (String) principal;
        if ((email == null || email.isBlank()) && auth.getName() != null) email = auth.getName();

        if (email != null && !email.isBlank()) {
            return userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Notification user not found by email"));
        }

        throw new RuntimeException("Unable to resolve current user");
    }

    @Override
    public List<NotificationDto> getMyNotifications() {
        NotificationUser user = resolveCurrentUser();
        List<Notification> list = notificationRepo.findAllByRecipientOrderByCreatedAtDesc(user);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        NotificationUser user = resolveCurrentUser();
        List<Notification> unread = notificationRepo.findAllByRecipientAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(unread);
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Integer notificationId) {
        NotificationUser user = resolveCurrentUser();
        Optional<Notification> opt = notificationRepo.findById(notificationId);
        Notification notification = opt.orElseThrow(() -> new RuntimeException("Notification not found"));
        if (notification.getRecipient() == null || !notification.getRecipient().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to modify this notification");
        }
        notification.setRead(true);
        Notification saved = notificationRepo.save(notification);
        return toDto(saved);
    }

    @Override
    public long getUnreadCount() {
        NotificationUser user = resolveCurrentUser();
        return notificationRepo.countByRecipientAndIsReadFalse(user);
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .reference(n.getReference())
                .build();
    }
}
