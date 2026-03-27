package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.CreateUserRequestDTO;
import com.revconnect.dto.LoginRequestDTO;
import com.revconnect.dto.UserResponseDTO;
import com.revconnect.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        token
                )
        );
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(
            @Valid @RequestBody CreateUserRequestDTO request) {

        UserResponseDTO response = authService.register(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User registered successfully",
                        response
                )
        );
    }
}