package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.HashtagResponseDTO;
import com.revconnect.entity.Hashtag;
import com.revconnect.service.HashtagService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<HashtagResponseDTO>>> getTrending() {

        List<HashtagResponseDTO> response = hashtagService.getTrendingHashtags()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Trending hashtags fetched successfully", response)
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<HashtagResponseDTO>> getByName(
            @PathVariable String name) {

        Hashtag hashtag = hashtagService.getByName(name);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Hashtag fetched successfully", mapToResponse(hashtag))
        );
    }

    private HashtagResponseDTO mapToResponse(Hashtag hashtag) {
        return HashtagResponseDTO.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .usageCount(hashtag.getUsageCount()) // if exists
                .build();
    }
}