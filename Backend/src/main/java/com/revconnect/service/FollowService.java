package com.revconnect.service;

import com.revconnect.entity.User;

import java.util.List;

public interface FollowService {

    void followUser(Long followerId, Long followingId);

    void unfollowUser(Long followerId, Long followingId);

    List<User> getFollowers(Long userId);

    List<User> getFollowing(Long userId);

    Long getFollowerCount(Long userId);

    Long getFollowingCount(Long userId);
}