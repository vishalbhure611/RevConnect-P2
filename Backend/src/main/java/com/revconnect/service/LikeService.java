package com.revconnect.service;

public interface LikeService {
    void likePost(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);
}
