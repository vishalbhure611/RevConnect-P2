package com.revconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Identifier is required")
    private String identifier; // email or username

    @NotBlank(message = "Password is required")
    private String password;
}