package com.revconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revconnect.entity.ProfilePrivacy;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDTO {

    private Long userId;
    private String username;
    private String role;

    private String fullName;
    private String bio;
    private String location;
    private String website;
    private String profilePictureUrl;

    private ProfilePrivacy privacy;

    // Business / Creator fields
    private String category;
    private String contactEmail;
    private String contactPhone;
    private String businessAddress;
    private String businessHours;

    private Long followersCount;
    private Long followingCount;
    private Long connectionsCount;

    @JsonProperty("isOwner")
    private boolean isOwner;

    @JsonProperty("isFollowing")
    private boolean isFollowing;

    @JsonProperty("isConnected")
    private boolean isConnected;

    private String connectionStatus;

    private String externalLinks;
}
