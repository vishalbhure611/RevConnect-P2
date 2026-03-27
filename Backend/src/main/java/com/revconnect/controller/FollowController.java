package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.UserResponseDTO;
import com.revconnect.entity.User;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.FollowService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // ================= FOLLOW =================

    @PostMapping("/{followingId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long followerId = userDetails.getUserId();

        followService.followUser(followerId, followingId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Followed successfully", null)
        );
    }

    // ================= UNFOLLOW =================

    @DeleteMapping("/{followingId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long followerId = userDetails.getUserId();

        followService.unfollowUser(followerId, followingId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Unfollowed successfully", null)
        );
    }

    // ================= GET FOLLOWERS =================

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getFollowers(
            @PathVariable Long userId) {

        List<User> followers = followService.getFollowers(userId);

        List<UserResponseDTO> response = followers.stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Followers fetched successfully", response)
        );
    }

    // ================= GET FOLLOWING =================

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getFollowing(
            @PathVariable Long userId) {

        List<User> following = followService.getFollowing(userId);

        List<UserResponseDTO> response = following.stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Following fetched successfully", response)
        );
    }
}