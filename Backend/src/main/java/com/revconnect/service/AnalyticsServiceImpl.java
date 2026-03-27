package com.revconnect.service;

import com.revconnect.entity.PostAnalytics;
import com.revconnect.repository.PostAnalyticsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PostAnalyticsRepository analyticsRepository;

    private PostAnalytics getAnalytics(Long postId) {
        return analyticsRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));
    }

    @Override
    public void incrementLikes(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
    }

    @Override
    public void decrementLikes(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalLikes(Math.max(0, analytics.getTotalLikes() - 1));
    }

    @Override
    public void incrementComments(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalComments(analytics.getTotalComments() + 1);
    }

    @Override
    public void decrementComments(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalComments(Math.max(0, analytics.getTotalComments() - 1));
    }

    @Override
    public void incrementShares(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalShares(analytics.getTotalShares() + 1);
    }

    @Override
    public void decrementShares(Long postId) {
        PostAnalytics analytics = getAnalytics(postId);
        analytics.setTotalShares(Math.max(0, analytics.getTotalShares() - 1));
    }
}