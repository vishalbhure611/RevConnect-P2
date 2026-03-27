package com.revconnect.service;

import com.revconnect.entity.Connection;

import java.util.List;

public interface ConnectionService {

    void sendConnectionRequest(Long senderId, Long receiverId);

    void acceptConnection(Long connectionId);

    void rejectConnection(Long connectionId);

    void removeConnection(Long connectionId);

    List<Connection> getPendingRequests(Long userId);

    List<Connection> getPendingSentRequests(Long userId);

    List<Connection> getConnections(Long userId);
}