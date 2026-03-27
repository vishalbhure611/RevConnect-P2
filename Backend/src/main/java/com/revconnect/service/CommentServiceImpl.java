package com.revconnect.service;

import com.revconnect.entity.Comment;
import com.revconnect.entity.NotificationType;
import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.entity.User;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostAnalyticsRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostAnalyticsRepository analyticsRepository;
    private final NotificationService notificationService;

    // ================= ADD COMMENT =================

    @Override
    public Comment addComment(Long userId, Long postId, String content) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

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

        analytics.setTotalComments(analytics.getTotalComments() + 1);

        // 🔔 Notify post owner (avoid self-notification)
        if (!post.getUser().getId().equals(userId)) {
            notificationService.createNotification(
                    post.getUser().getId(),   // receiver
                    userId,                  // triggered by
                    postId,
                    NotificationType.COMMENT
            );
        }

        return savedComment;
    }

    // ================= DELETE COMMENT =================

    @Override
    public void deleteComment(Long commentId, Long requestUserId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Long commentOwnerId = comment.getUser().getId();
        Long postOwnerId = comment.getPost().getUser().getId();

        // Only comment owner OR post owner can delete
        if (!requestUserId.equals(commentOwnerId)
                && !requestUserId.equals(postOwnerId)) {

            throw new RuntimeException("Not authorized to delete this comment");
        }

        PostAnalytics analytics = analyticsRepository
                .findByPostId(comment.getPost().getId())
                .orElse(null);

        if (analytics != null) {
            analytics.setTotalComments(
                    Math.max(0, analytics.getTotalComments() - 1)
            );
        }

        commentRepository.delete(comment);
    }

    // ================= GET COMMENTS =================

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }
}