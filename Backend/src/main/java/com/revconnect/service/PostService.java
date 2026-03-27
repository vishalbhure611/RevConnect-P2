package com.revconnect.service;

import com.revconnect.dto.PostRequestDTO;
import com.revconnect.entity.Post;

import java.util.List;

public interface PostService {

    Post createPost(String username, PostRequestDTO request);

    Post updatePost(Long postId, String username, PostRequestDTO request);

    void deletePost(Long postId, String username);

    Post setPinned(Long postId, String username, boolean pinned);

    Post getPostById(Long postId);

    List<Post> getPostsByUser(Long userId);

    List<Post> getFeedForUser(Long userId);

    Post repost(Long userId, Long originalPostId, String caption);

    List<Post> getPostsByHashtag(String hashtag);
}