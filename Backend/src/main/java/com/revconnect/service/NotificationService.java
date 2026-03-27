package com.revconnect.service;

import com.revconnect.entity.Notification;
import com.revconnect.entity.NotificationType;

import java.util.List;

public interface NotificationService {

    void createNotification(
            Long receiverId,
            Long triggeredById,
            Long postId,
            NotificationType type
    );

    List<Notification> getNotifications(Long userId);

    Long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
