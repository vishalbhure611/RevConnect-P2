package com.revconnect.controller;

import com.revconnect.dto.ApiResponse;
import com.revconnect.dto.ConnectionResponseDTO;
import com.revconnect.entity.Connection;
import com.revconnect.service.ConnectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request/{senderId}/{receiverId}")
    public ResponseEntity<ApiResponse<Void>> sendRequest(
            @PathVariable Long senderId,
            @PathVariable Long receiverId
    ) {
        connectionService.sendConnectionRequest(senderId, receiverId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Connection request sent", null)
        );
    }

    @PutMapping("/accept/{connectionId}")
    public ResponseEntity<ApiResponse<Void>> acceptRequest(
            @PathVariable Long connectionId) {

        connectionService.acceptConnection(connectionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Connection accepted", null)
        );
    }

    @PutMapping("/reject/{connectionId}")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(
            @PathVariable Long connectionId) {

        connectionService.rejectConnection(connectionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Connection rejected", null)
        );
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<ApiResponse<Void>> removeConnection(
            @PathVariable Long connectionId) {

        connectionService.removeConnection(connectionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Connection removed", null)
        );
    }

    @GetMapping("/pending/received/{userId}")
    public ResponseEntity<ApiResponse<List<ConnectionResponseDTO>>> getPendingReceived(
            @PathVariable Long userId
    ) {
        List<ConnectionResponseDTO> response = connectionService.getPendingRequests(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Pending received requests fetched", response)
        );
    }

    @GetMapping("/pending/sent/{userId}")
    public ResponseEntity<ApiResponse<List<ConnectionResponseDTO>>> getPendingSent(
            @PathVariable Long userId
    ) {
        List<ConnectionResponseDTO> response = connectionService.getPendingSentRequests(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Pending sent requests fetched", response)
        );
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<ApiResponse<List<ConnectionResponseDTO>>> getConnections(
            @PathVariable Long userId
    ) {
        List<ConnectionResponseDTO> response = connectionService.getConnections(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Connections fetched", response)
        );
    }

    private ConnectionResponseDTO map(Connection c) {
        return ConnectionResponseDTO.builder()
                .id(c.getId())
                .senderId(c.getSender().getId())
                .receiverId(c.getReceiver().getId())
                .senderUsername(c.getSender().getUsername())
                .receiverUsername(c.getReceiver().getUsername())
                .status(c.getStatus().name())
                .build();
    }
}
