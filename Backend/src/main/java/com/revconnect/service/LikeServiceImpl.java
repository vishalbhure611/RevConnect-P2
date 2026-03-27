package com.revconnect.service;

import com.revconnect.entity.*;
import com.revconnect.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostAnalyticsRepository analyticsRepository;
    private final NotificationService notificationService;

    // ================= LIKE =================

    @Override
    public void likePost(Long userId, Long postId) {

        if (likeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return; // already liked
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(like);

        // 🔥 Ensure analytics exists
        PostAnalytics analytics = analyticsRepository.findByPostId(postId)
                .orElseGet(() -> {
                    PostAnalytics newAnalytics = PostAnalytics.builder()
                            .post(post)
                            .totalLikes(0)
                            .totalComments(0)
                            .totalShares(0)
                            .build();
                    return analyticsRepository.save(newAnalytics);
                });

        analytics.setTotalLikes(analytics.getTotalLikes() + 1);

        // 🔔 Notify post owner (avoid self-notification)
        if (!post.getUser().getId().equals(userId)) {
            notificationService.createNotification(
                    post.getUser().getId(),
                    userId,
                    postId,
                    NotificationType.LIKE
            );
        }
    }

    // ================= UNLIKE =================

    @Override
    public void unlikePost(Long userId, Long postId) {

        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);

        PostAnalytics analytics = analyticsRepository.findByPostId(postId)
                .orElse(null);

        if (analytics != null) {
            analytics.setTotalLikes(
                    Math.max(0, analytics.getTotalLikes() - 1)
            );
        }
    }
}