package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import com.revconnect.entity.NotificationType;
import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public void sendConnectionRequest(Long senderId, Long receiverId) {

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot connect to yourself");
        }

        if (connectionRepository.existsBySenderIdAndReceiverId(senderId, receiverId)) {
            throw new RuntimeException("Connection request already exists");
        }

        if (connectionRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new RuntimeException("Connection request already exists");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Connection connection = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        connectionRepository.save(connection);

        notificationService.createNotification(
                receiverId,
                senderId,
                null,
                NotificationType.CONNECTION_REQUEST
        );
    }

    @Override
    public void acceptConnection(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connection.setStatus(ConnectionStatus.ACCEPTED);

        connectionRepository.save(connection);

        notificationService.createNotification(
                connection.getSender().getId(),
                connection.getReceiver().getId(),
                null,
                NotificationType.CONNECTION_ACCEPTED
        );
    }

    @Override
    public void rejectConnection(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connection.setStatus(ConnectionStatus.REJECTED);

        connectionRepository.save(connection);
    }

    @Override
    public void removeConnection(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connectionRepository.delete(connection);
    }

    @Override
    public List<Connection> getPendingRequests(Long userId) {
        return connectionRepository
                .findByReceiverIdAndStatus(userId, ConnectionStatus.PENDING);
    }

    @Override
    public List<Connection> getPendingSentRequests(Long userId) {
        return connectionRepository
                .findBySenderIdAndStatus(userId, ConnectionStatus.PENDING);
    }

    @Override
    public List<Connection> getConnections(Long userId) {

        List<Connection> result = new ArrayList<>();

        result.addAll(
                connectionRepository.findBySenderIdAndStatus(userId, ConnectionStatus.ACCEPTED)
        );

        result.addAll(
                connectionRepository.findByReceiverIdAndStatus(userId, ConnectionStatus.ACCEPTED)
        );

        return result;
    }
}
