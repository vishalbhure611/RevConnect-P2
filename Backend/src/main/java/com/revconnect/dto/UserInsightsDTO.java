package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInsightsDTO {
    private Long userId;
    private long totalPosts;
    private long totalLikes;
    private long totalComments;
    private long totalShares;
    private long followerCount;
    private long followingCount;
}

