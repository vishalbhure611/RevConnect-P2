package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.service.ShareService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @PostMapping("/{postId}/{userId}")
    public ResponseEntity<ApiResponse<Void>> sharePost(
            @PathVariable Long postId,
            @PathVariable Long userId
    ) {
        shareService.sharePost(userId, postId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post shared successfully", null)
        );
    }

    @DeleteMapping("/{postId}/{userId}")
    public ResponseEntity<ApiResponse<Void>> unsharePost(
            @PathVariable Long postId,
            @PathVariable Long userId
    ) {
        shareService.unsharePost(userId, postId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post unshared successfully", null)
        );
    }
}