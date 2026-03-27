package com.revconnect.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HashtagResponseDTO {

    private Long id;
    private String name;
    private Long usageCount; // optional if you track it
}