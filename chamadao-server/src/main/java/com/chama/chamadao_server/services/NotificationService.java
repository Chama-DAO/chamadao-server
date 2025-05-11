package com.chama.chamadao_server.services;

import com.chama.chamadao_server.models.Notification;
import com.chama.chamadao_server.models.enums.NotificationType;
import com.chama.chamadao_server.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public Notification createChamaInvitation(String senderWalletAddress, String receiverWalletAddress,
                                              String chamaWalletAddress, String chamaName) {
        log.info("Creating invitation notification from {} to {} for Chama {}",
                senderWalletAddress, receiverWalletAddress, chamaWalletAddress);

        // Create notification
        Notification notification = Notification.builder()
                .senderWalletAddress(senderWalletAddress)
                .receiverWalletAddress(receiverWalletAddress)
                .chamaWalletAddress(chamaWalletAddress)
                .chamaName(chamaName)
                .type(NotificationType.CHAMA_INVITATION)
                .message("You have been invited to join " + chamaName)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Send WebSocket notification
        sendWebSocketNotification(receiverWalletAddress, saved);

        return saved;
    }

    private void sendWebSocketNotification(String receiverWalletAddress, Notification notification) {
        try {
            // Send to specific user's queue
            messagingTemplate.convertAndSendToUser(
                    receiverWalletAddress,
                    "/queue/notifications",
                    notification
            );

            // Also broadcast to a topic (optional)
            messagingTemplate.convertAndSend(
                    "/topic/notifications." + receiverWalletAddress,
                    notification
            );

            log.info("WebSocket notification sent to {}", receiverWalletAddress);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage(), e);
        }
    }

    public List<Notification> getUserNotifications(String walletAddress) {
        return notificationRepository.findByReceiverWalletAddressOrderByCreatedAtDesc(walletAddress);
    }

    public List<Notification> getUnreadNotifications(String walletAddress) {
        return notificationRepository.findByReceiverWalletAddressAndReadFalseOrderByCreatedAtDesc(walletAddress);
    }

    public long getUnreadCount(String walletAddress) {
        return notificationRepository.countByReceiverWalletAddressAndReadFalse(walletAddress);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
