package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceDTO {
    private String type;
    private boolean enabled;
}

