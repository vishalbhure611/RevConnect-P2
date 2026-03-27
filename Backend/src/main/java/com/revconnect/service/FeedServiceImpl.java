package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.Follow;
import com.revconnect.entity.Post;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.PostRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final ConnectionRepository connectionRepository;
    private final FollowRepository followRepository;

    @Override
    public List<Post> getUserFeed(Long userId) {

        Set<Long> feedUserIds = new HashSet<>();

        // Add own ID
        feedUserIds.add(userId);

        // Add connected users
        List<Connection> connections = connectionRepository
                .findByReceiverIdAndStatus(userId, ConnectionStatus.ACCEPTED);

        for (Connection connection : connections) {
            feedUserIds.add(connection.getSender().getId());
        }

        // Add followed users
        List<Follow> follows = followRepository.findByFollowerId(userId);

        for (Follow follow : follows) {
            feedUserIds.add(follow.getFollowing().getId());
        }

        // Get all posts from those users
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .filter(post -> feedUserIds.contains(post.getUser().getId()))
                .filter(post -> post.getScheduledTime() == null ||
                        post.getScheduledTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}