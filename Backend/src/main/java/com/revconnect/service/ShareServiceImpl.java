package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.entity.NotificationType;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ShareRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.PostAnalyticsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostAnalyticsRepository analyticsRepository;
    private final NotificationService notificationService;

    @Override
    public void sharePost(Long userId, Long postId) {

        if (shareRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Share share = Share.builder()
                .user(user)
                .post(post)
                .build();

        shareRepository.save(share);

        PostAnalytics analytics = analyticsRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));

        analytics.setTotalShares(analytics.getTotalShares() + 1);

        // Notify post owner
        if (!post.getUser().getId().equals(userId)) {
            notificationService.createNotification(
                    post.getUser().getId(),
                    userId,
                    postId,
                    NotificationType.SHARE
            );
        }
    }

    @Override
    public void unsharePost(Long userId, Long postId) {

        shareRepository.deleteByUserIdAndPostId(userId, postId);

        PostAnalytics analytics = analyticsRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));

        analytics.setTotalShares(Math.max(0, analytics.getTotalShares() - 1));
    }
}