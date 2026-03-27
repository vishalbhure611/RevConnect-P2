package com.revconnect.service;

public interface AnalyticsService {

    void incrementLikes(Long postId);

    void decrementLikes(Long postId);

    void incrementComments(Long postId);

    void decrementComments(Long postId);

    void incrementShares(Long postId);

    void decrementShares(Long postId);
}