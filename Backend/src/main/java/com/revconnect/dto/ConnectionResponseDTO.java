package com.revconnect.dto;

import lombok.*;

@Data
@Builder
public class ConnectionResponseDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderUsername;
    private String receiverUsername;
    private String status;
}
