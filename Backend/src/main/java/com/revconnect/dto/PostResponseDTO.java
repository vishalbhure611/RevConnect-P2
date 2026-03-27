package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponseDTO {

    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledTime;
    private String postType;
    private Boolean pinned;

    private String ctaLabel;
    private String ctaUrl;

    private Long originalPostId;
    private String originalUsername;
    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private List<String> hashtags;

    private List<Long> productIds;
    private List<String> productNames;
}