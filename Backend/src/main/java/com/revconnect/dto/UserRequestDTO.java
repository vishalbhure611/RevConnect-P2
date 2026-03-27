package com.revconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.revconnect.entity.Role;

@Data
public class UserRequestDTO {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role; // PERSONAL, CREATOR, BUSINESS (optional; defaults to PERSONAL)
}