package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowerDemographicsDTO {
    private Long userId;
    private long totalFollowers;
    private long personalFollowers;
    private long creatorFollowers;
    private long businessFollowers;
    private long newFollowersLast30Days;
}
