package com.revconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String url;
    private Double price;
}

