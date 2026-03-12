package com.banking.notificationservice.controller;

import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.exceptions.ApiResponse;
import com.banking.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationDto>> getMyNotifications() {
        return ApiResponse.success(200, "Notifications fetched", notificationService.getMyNotifications());
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.success(200, "Unread count fetched", notificationService.getUnreadCount());
    }

    @PatchMapping("/read")
    public ApiResponse<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success(200, "All notifications marked as read", "OK");
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationDto> markAsRead(@PathVariable Integer id) {
        return ApiResponse.success(200, "Notification marked as read", notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ApiResponse.success(200, "Notification deleted successfully", "OK");
    }

}
