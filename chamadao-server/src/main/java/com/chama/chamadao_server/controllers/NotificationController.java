package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.Notification;
import com.chama.chamadao_server.models.dto.ChamaDto;
import com.chama.chamadao_server.services.ChamaService;
import com.chama.chamadao_server.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final ChamaService chamaService;
    //private final ChamaMapper chamaMapper;

    @GetMapping("/{walletAddress}")
    ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String walletAddress){
        log.info("Request to get notifications for user {}", walletAddress);
        List<Notification> notifications = notificationService.getUserNotifications(walletAddress);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{walletAddress}/unread")
    ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String walletAddress){
        log.info("Request to get unread notifications for user {}", walletAddress);
        List<Notification> notifications = notificationService.getUnreadNotifications(walletAddress);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{walletAddress}/unread/count")
    ResponseEntity<Long> getUnreadCount(@PathVariable String walletAddress){
        log.info("Request to get unread notification count for user {}", walletAddress);
        long count = notificationService.getUnreadCount(walletAddress);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/invite")
    public ResponseEntity<Notification> inviteUserToChama(
            @RequestParam String senderWalletAddress,
            @RequestParam String receiverWalletAddress,
            @RequestParam String chamaWalletAddress) {

        // Get chama name
        ChamaDto chama = chamaService.findChamaByWalletAddress(chamaWalletAddress);

        Notification notification = notificationService.createChamaInvitation(
                senderWalletAddress, receiverWalletAddress, chamaWalletAddress, chama.getName());

        return ResponseEntity.ok(notification);
    }


    @PostMapping("/{notificationId}/accept")
    public ResponseEntity<ChamaDto> acceptChamaInvitation(@PathVariable Long notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);

        ChamaDto chama = chamaService.addMemberToChama(
                notification.getChamaWalletAddress(),
                notification.getReceiverWalletAddress()
        );
        return ResponseEntity.ok(chama);
    }
    @PostMapping("/{notificationId}/reject")
    public ResponseEntity<Void> rejectChamaInvitation(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }
}
