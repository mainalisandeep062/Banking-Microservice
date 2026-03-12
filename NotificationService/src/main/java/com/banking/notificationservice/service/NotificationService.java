package com.banking.notificationservice.service;

import com.banking.notificationservice.dto.NotificationDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getMyNotifications();

    void markAllAsRead();

    NotificationDto markAsRead(Integer notificationId);

    long getUnreadCount();

    void deleteNotification(Integer notificationId);

}
