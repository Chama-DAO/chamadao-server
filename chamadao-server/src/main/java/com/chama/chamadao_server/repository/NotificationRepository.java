package com.chama.chamadao_server.repository;

import com.chama.chamadao_server.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverWalletAddressOrderByCreatedAtDesc(String receiverWalletAddress);
    List<Notification> findByReceiverWalletAddressAndReadFalseOrderByCreatedAtDesc(String receiverWalletAddress);
    long countByReceiverWalletAddressAndReadFalse(String receiverWalletAddress);
}