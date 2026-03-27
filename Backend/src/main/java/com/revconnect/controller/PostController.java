package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.PostRequestDTO;
import com.revconnect.dto.PostResponseDTO;
import com.revconnect.entity.Post;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDTO>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequestDTO request
    ) {

        Post post = postService.createPost(userDetails.getUsername(), request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post created successfully", mapToResponse(post))
        );
    }

    // ================= GET =================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> getPost(@PathVariable Long id) {

        Post post = postService.getPostById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post fetched successfully", mapToResponse(post))
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getPostsByUser(
            @PathVariable Long userId
    ) {

        List<PostResponseDTO> response = postService.getPostsByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Posts fetched successfully", response)
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDTO>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequestDTO request
    ) {

        Post updated = postService.updatePost(postId, userDetails.getUsername(), request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post updated successfully", mapToResponse(updated))
        );
    }

    // ================= DELETE =================

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        postService.deletePost(postId, userDetails.getUsername());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post deleted successfully", null)
        );
    }

    // ================= PIN =================

    @PutMapping("/{postId}/pin")
    public ResponseEntity<ApiResponse<PostResponseDTO>> pinPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam boolean value
    ) {

        Post updated = postService.setPinned(postId, userDetails.getUsername(), value);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Pin status updated successfully", mapToResponse(updated))
        );
    }
    @PostMapping("/{postId}/repost")
    public ResponseEntity<ApiResponse<PostResponseDTO>> repost(
            @PathVariable Long postId,
            @RequestParam(required = false) String caption,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Post repost = postService.repost(
                userDetails.getUserId(),
                postId,
                caption
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post reposted successfully", mapToResponse(repost))
        );
    }
    @GetMapping("/hashtag/{name}")
    public ResponseEntity<ApiResponse<List<PostResponseDTO>>> getPostsByHashtag(
            @PathVariable String name
    ) {

        List<PostResponseDTO> response = postService
                .getPostsByHashtag(name)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Posts fetched successfully", response)
        );
    }
    // ================= RESPONSE MAPPER =================

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
                .totalLikes(post.getAnalytics() != null ? post.getAnalytics().getTotalLikes() : 0)
                .totalComments(post.getAnalytics() != null ? post.getAnalytics().getTotalComments() : 0)
                .totalShares(post.getAnalytics() != null ? post.getAnalytics().getTotalShares() : 0)
                .build();
    }
}