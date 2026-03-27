package com.revconnect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponseDTO {

    private Long id;
    private String type;
    private String triggeredBy;
    private Long postId;
    @JsonProperty("isRead")
    private boolean isRead;
}
