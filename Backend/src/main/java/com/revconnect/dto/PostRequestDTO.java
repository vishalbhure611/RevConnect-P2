package com.revconnect.dto;

import lombok.Data;
import com.revconnect.entity.PostType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostRequestDTO {

    private String content;
    private LocalDateTime scheduledTime;

    private PostType postType;
    private Boolean pinned;

    private String ctaLabel;
    private String ctaUrl;

    private Long originalPostId; // for reposts

    private List<Long> productIds; // tagged products/services
}