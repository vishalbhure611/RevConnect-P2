package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.NotificationPreferenceDTO;
import com.revconnect.dto.NotificationResponseDTO;
import com.revconnect.entity.Notification;
import com.revconnect.entity.NotificationType;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.NotificationPreferenceService;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        List<NotificationResponseDTO> response =
                notificationService.getNotifications(userId)
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notifications fetched successfully", response)
        );
    }

    // ================= UNREAD COUNT =================

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long count = notificationService.getUnreadCount(userDetails.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Unread count fetched successfully", count)
        );
    }

    // ================= MARK READ =================

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAsRead(notificationId, userDetails.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification marked as read", null)
        );
    }

    @PutMapping("/read/all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAllAsRead(userDetails.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "All notifications marked as read", null)
        );
    }

    // ================= PREFERENCES =================

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceDTO>>> getPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        List<NotificationPreferenceDTO> response =
                preferenceService.getPreferences(userDetails.getUserId())
                        .stream()
                        .map(p -> NotificationPreferenceDTO.builder()
                                .type(p.getType().name())
                                .enabled(p.isEnabled())
                                .build())
                        .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification preferences fetched", response)
        );
    }

    @PutMapping("/preferences/{type}")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> setPreference(
            @PathVariable NotificationType type,
            @RequestBody NotificationPreferenceDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        var saved = preferenceService.setPreference(
                userDetails.getUserId(),
                type,
                request.isEnabled()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification preference updated",
                        NotificationPreferenceDTO.builder()
                                .type(saved.getType().name())
                                .enabled(saved.isEnabled())
                                .build()
                )
        );
    }

    private NotificationResponseDTO mapToResponse(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType().name())
                .triggeredBy(n.getTriggeredBy().getUsername())
                .postId(n.getPost() != null ? n.getPost().getId() : null)
                .isRead(n.isRead())
                .build();
    }
}
