package com.revconnect.service;

public interface ShareService {

    void sharePost(Long userId, Long postId);

    void unsharePost(Long userId, Long postId);
}