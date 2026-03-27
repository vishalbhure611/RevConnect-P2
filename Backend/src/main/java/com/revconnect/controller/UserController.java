package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.UpdateAccountRequestDTO;
import com.revconnect.dto.UserRequestDTO;
import com.revconnect.dto.UserResponseDTO;
import com.revconnect.entity.User;
import com.revconnect.entity.Privacy;
import com.revconnect.exception.UnauthorizedException;
import com.revconnect.security.CustomUserDetails;
import com.revconnect.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create user
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // encryption in Phase 2
        user.setRole(request.getRole());

        User savedUser = userService.createUser(user);

        UserResponseDTO response = UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User created successfully", response)
        );
    }

    // Get user
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@PathVariable Long id) {

        User user = userService.getUserById(id);

        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User fetched successfully", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !userDetails.getUserId().equals(id)) {
            throw new UnauthorizedException("Not authorized to update this account");
        }

        User user = userService.updateAccount(id, request);

        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Account updated successfully", response)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "query", required = false) String query
    ) {
        String searchTerm = (keyword != null && !keyword.isBlank()) ? keyword : query;

        List<UserResponseDTO> response = userService.searchUsers(searchTerm)
                .stream()
                .map(u -> UserResponseDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users fetched successfully", response)
        );
    }

    @PutMapping("/{id}/privacy")
    public ResponseEntity<ApiResponse<Void>> updatePrivacy(
            @PathVariable Long id,
            @RequestParam("privacy") Privacy privacy
    ) {
        userService.updatePrivacy(id, privacy);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Privacy updated successfully", null)
        );
    }
}
