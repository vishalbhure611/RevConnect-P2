package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.LikeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        likeService.likePost(userDetails.getUserId(), postId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post liked successfully", null)
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        likeService.unlikePost(userDetails.getUserId(), postId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post unliked successfully", null)
        );
    }
}