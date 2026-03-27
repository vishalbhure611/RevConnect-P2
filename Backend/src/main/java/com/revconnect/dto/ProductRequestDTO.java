package com.revconnect.dto;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private String url;
    private Double price;
}

