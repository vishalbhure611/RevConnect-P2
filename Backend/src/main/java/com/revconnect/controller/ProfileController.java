package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.ProfileRequestDTO;
import com.revconnect.dto.ProfileResponseDTO;
import com.revconnect.entity.Profile;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Fetch profile by USERNAME
     * Example:
     * /api/profile/username/vishal
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfileByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long viewerId = userDetails != null ? userDetails.getUserId() : null;

        ProfileResponseDTO profile =
                profileService.getProfileByUsername(viewerId, username);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile fetched successfully",
                        profile
                )
        );
    }

    /**
     * Fetch profile by USER ID
     * Useful internally
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfileByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long viewerId = userDetails != null ? userDetails.getUserId() : null;

        ProfileResponseDTO profile =
                profileService.getProfile(viewerId, userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile fetched successfully",
                        profile
                )
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long viewerId = userDetails != null ? userDetails.getUserId() : null;
        if (viewerId == null || !viewerId.equals(userId)) {
            throw new RuntimeException("Not authorized to update this profile");
        }

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setPrivacy(request.getPrivacy());
        profile.setCategory(request.getCategory());
        profile.setContactEmail(request.getContactEmail());
        profile.setContactPhone(request.getContactPhone());
        profile.setBusinessAddress(request.getBusinessAddress());
        profile.setBusinessHours(request.getBusinessHours());
        profile.setExternalLinks(request.getExternalLinks());

        profileService.updateProfile(userId, profile);

        ProfileResponseDTO response = profileService.getProfile(viewerId, userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile updated successfully",
                        response
                )
        );
    }

}
