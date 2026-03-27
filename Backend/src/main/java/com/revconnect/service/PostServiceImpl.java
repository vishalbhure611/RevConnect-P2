package com.revconnect.service;

import com.revconnect.dto.PostRequestDTO;
import com.revconnect.entity.*;
import com.revconnect.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostAnalyticsRepository analyticsRepository;
    private final NotificationService notificationService;

    // ================= CREATE =================

    @Override
    public Post createPost(String username, PostRequestDTO request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .content(request.getContent())
                .user(user)
                .postType(request.getPostType() == null ? PostType.NORMAL : request.getPostType())
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .ctaLabel(request.getCtaLabel())
                .ctaUrl(request.getCtaUrl())
                .scheduledTime(request.getScheduledTime())
                .published(true)
                .build();

        return postRepository.save(post);
    }

    // ================= REPOST =================

    @Override
    public Post repost(Long userId, Long originalPostId, String caption) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post originalPost = postRepository.findById(originalPostId)
                .orElseThrow(() -> new RuntimeException("Original post not found"));

        Post repost = Post.builder()
                .content(caption) // optional caption
                .user(user)
                .originalPost(originalPost)
                .postType(PostType.NORMAL)
                .published(true)
                .build();

        Post savedRepost = postRepository.save(repost);

        // 🔥 Increment share count
        PostAnalytics analytics = analyticsRepository.findByPostId(originalPostId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));

        analytics.setTotalShares(analytics.getTotalShares() + 1);

        // 🔥 Notify original owner
        if (!originalPost.getUser().getId().equals(userId)) {
            notificationService.createNotification(
                    originalPost.getUser().getId(),
                    userId,
                    originalPostId,
                    NotificationType.SHARE
            );
        }

        return savedRepost;
    }

    // ================= UPDATE =================

    @Override
    public Post updatePost(Long postId, String username, PostRequestDTO request) {

        Post post = getPostById(postId);

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to update this post");
        }

        if (request.getContent() != null)
            post.setContent(request.getContent());

        if (request.getPostType() != null)
            post.setPostType(request.getPostType());

        if (request.getPinned() != null)
            post.setPinned(request.getPinned());

        post.setCtaLabel(request.getCtaLabel());
        post.setCtaUrl(request.getCtaUrl());
        post.setScheduledTime(request.getScheduledTime());

        return postRepository.save(post);
    }

    // ================= DELETE =================

    @Override
    public void deletePost(Long postId, String username) {

        Post post = getPostById(postId);

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    // ================= PIN =================

    @Override
    public Post setPinned(Long postId, String username, boolean pinned) {

        Post post = getPostById(postId);

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }

        post.setPinned(pinned);

        return postRepository.save(post);
    }

    // ================= GET =================

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByUserIdOrderByPinnedDescCreatedAtDesc(userId);
    }

    // ================= MIXED FEED =================

    @Override
    public List<Post> getFeedForUser(Long userId) {

        List<Post> allPosts = postRepository.findAllPublishedOrderByCreatedAtDesc();

        List<Long> followingIds = followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> f.getFollowing().getId())
                .collect(Collectors.toList());

        followingIds.add(userId);

        Set<Long> priorityUsers = new HashSet<>(followingIds);

        return allPosts.stream()
                .sorted((p1, p2) -> {

                    boolean p1Priority = priorityUsers.contains(p1.getUser().getId());
                    boolean p2Priority = priorityUsers.contains(p2.getUser().getId());

                    if (p1Priority && !p2Priority) return -1;
                    if (!p1Priority && p2Priority) return 1;

                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<Post> getPostsByHashtag(String hashtag) {

        if (hashtag == null || hashtag.isBlank()) {
            throw new RuntimeException("Hashtag cannot be empty");
        }

        // Remove '#' if user sends it
        String cleanTag = hashtag.startsWith("#")
                ? hashtag.substring(1)
                : hashtag;

        return postRepository.findPublishedByHashtag(cleanTag);
    }
}