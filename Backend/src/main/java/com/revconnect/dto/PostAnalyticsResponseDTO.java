package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostAnalyticsResponseDTO {
    private Long postId;
    private long totalLikes;
    private long totalComments;
    private long totalShares;
    private long reach;
}

