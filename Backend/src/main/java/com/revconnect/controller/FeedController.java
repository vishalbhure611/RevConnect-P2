package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.PostResponseDTO;
import com.revconnect.entity.Post;
import com.revconnect.entity.PostType;
import com.revconnect.entity.Role;
import com.revconnect.service.FeedService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getFeed(
            @PathVariable Long userId,
            @RequestParam(value = "postType", required = false) PostType postType,
            @RequestParam(value = "userRole", required = false) Role userRole
    ) {

        List<PostResponseDTO> response = feedService.getUserFeed(userId)
                .stream()
                .filter(p -> postType == null || p.getPostType() == postType)
                .filter(p -> userRole == null || (p.getUser() != null && p.getUser().getRole() == userRole))
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Feed fetched successfully", response)
        );
    }

    private PostResponseDTO mapToResponse(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .username(post.getUser().getUsername())
                .createdAt(post.getCreatedAt())
                .scheduledTime(post.getScheduledTime())
                .postType(post.getPostType() != null ? post.getPostType().name() : null)
                .pinned(post.isPinned())
                .ctaLabel(post.getCtaLabel())
                .ctaUrl(post.getCtaUrl())
                .originalPostId(post.getOriginalPost() != null ? post.getOriginalPost().getId() : null)
                .originalUsername(
                        post.getOriginalPost() != null
                                ? post.getOriginalPost().getUser().getUsername()
                                : null
                )
                .totalLikes(post.getAnalytics() != null ? post.getAnalytics().getTotalLikes() : 0L)
                .totalComments(post.getAnalytics() != null ? post.getAnalytics().getTotalComments() : 0L)
                .totalShares(post.getAnalytics() != null ? post.getAnalytics().getTotalShares() : 0L)
                .productIds(
                        post.getProducts() == null
                                ? List.of()
                                : post.getProducts().stream().map(p -> p.getId()).collect(Collectors.toList())
                )
                .productNames(
                        post.getProducts() == null
                                ? List.of()
                                : post.getProducts().stream().map(p -> p.getName()).collect(Collectors.toList())
                )
                .build();
    }
}