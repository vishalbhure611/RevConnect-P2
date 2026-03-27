package com.revconnect.service;

import com.revconnect.entity.Post;

import java.util.List;

public interface FeedService {

    List<Post> getUserFeed(Long userId);
}