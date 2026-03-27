package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.CommentRequestDTO;
import com.revconnect.dto.CommentResponseDTO;
import com.revconnect.entity.Comment;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ================= ADD COMMENT =================

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentRequestDTO request
    ) {

        Comment comment = commentService.addComment(
                userDetails.getUserId(),
                postId,
                request.getContent()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Comment added successfully", mapToResponse(comment))
        );
    }

    // ================= DELETE COMMENT =================

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        commentService.deleteComment(commentId, userDetails.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Comment deleted successfully", null)
        );
    }

    // ================= GET COMMENTS =================

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getComments(
            @PathVariable Long postId) {

        List<CommentResponseDTO> response = commentService.getCommentsByPost(postId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Comments fetched successfully", response)
        );
    }

    private CommentResponseDTO mapToResponse(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}