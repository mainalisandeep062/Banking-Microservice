package com.banking.notificationservice.controller;

import com.banking.notificationservice.dto.NotificationDto;
import com.banking.notificationservice.exceptions.ApiResponse;
import com.banking.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        var list = notificationService.getMyNotifications();
        return ResponseEntity.ok(ApiResponse.success(200, "Notifications fetched", list));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.success(200, "Unread count fetched", count));
    }

    @PatchMapping("/read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success(200, "All notifications marked as read", "OK"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Integer id) {
        NotificationDto dto = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Notification marked as read", dto));
    }

}
