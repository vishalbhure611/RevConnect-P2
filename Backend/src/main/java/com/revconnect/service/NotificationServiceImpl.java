package com.revconnect.service;

import com.revconnect.entity.Notification;
import com.revconnect.entity.NotificationType;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationPreferenceService preferenceService;

    // ================= CREATE =================

    @Override
    public void createNotification(
            Long receiverId,
            Long triggeredById,
            Long postId,
            NotificationType type
    ) {

        // Prevent self-notification
        if (receiverId.equals(triggeredById)) {
            return;
        }

        // Check preference
        if (type != null && !preferenceService.isEnabled(receiverId, type)) {
            return;
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        User triggeredBy = userRepository.findById(triggeredById)
                .orElseThrow(() -> new RuntimeException("Triggering user not found"));

        Post post = null;

        if (postId != null) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .triggeredBy(triggeredBy)
                .post(post)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    // ================= GET ALL =================

    @Override
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    // ================= UNREAD COUNT =================

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository
                .countByReceiverIdAndIsReadFalse(userId);
    }

    // ================= MARK AS READ (SECURE) =================

    @Override
    public void markAsRead(Long notificationId, Long userId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to modify this notification");
        }

        notification.setRead(true);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .forEach(notification -> notification.setRead(true));
    }
}
