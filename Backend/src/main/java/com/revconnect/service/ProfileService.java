package com.revconnect.service;

import com.revconnect.dto.ProfileResponseDTO;
import com.revconnect.entity.Profile;

public interface ProfileService {

    /**
     * Create profile for a user
     */
    Profile createProfile(Long userId, Profile profile);

    /**
     * Update profile
     */
    Profile updateProfile(Long userId, Profile profile);

    /**
     * Basic fetch
     */
    Profile getProfileByUserId(Long userId);

    /**
     * Advanced fetch with privacy + connection check
     */
    ProfileResponseDTO getProfile(Long viewerId, Long profileOwnerId);

    ProfileResponseDTO getProfileByUsername(Long viewerId, String username);
}