package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Connection> findByReceiverIdAndStatus(Long receiverId, ConnectionStatus status);

    List<Connection> findBySenderIdAndStatus(Long senderId, ConnectionStatus status);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    long countBySenderIdAndStatus(Long senderId, ConnectionStatus status);

    long countByReceiverIdAndStatus(Long receiverId, ConnectionStatus status);
}
