package com.revconnect.service;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.entity.NotificationType;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ================= FOLLOW =================

    @Override
    public void followUser(Long followerId, Long followingId) {

        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("Already following this user");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        notificationService.createNotification(
                followingId,
                followerId,
                null,
                NotificationType.FOLLOW
        );
    }

    // ================= UNFOLLOW =================

    @Override
    public void unfollowUser(Long followerId, Long followingId) {

        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        followRepository.delete(follow);
    }

    // ================= GET FOLLOWERS =================

    @Override
    public List<User> getFollowers(Long userId) {

        return followRepository.findByFollowingId(userId)
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }

    // ================= GET FOLLOWING =================

    @Override
    public List<User> getFollowing(Long userId) {

        return followRepository.findByFollowerId(userId)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }

    // ================= COUNT =================

    @Override
    public Long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    public Long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }
}