package com.chama.chamadao_server.controllers;

import com.chama.chamadao_server.models.Notification;
import com.chama.chamadao_server.services.ChamaService;
import com.chama.chamadao_server.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final ChamaService chamaService;

    @GetMapping("/{walletAddress}")
    ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String walletAddress){
        log.info("Request to get notifications for user {}", walletAddress);
        List<Notification> notifications = notificationService.getUserNotifications(walletAddress);
        return ResponseEntity.ok(notifications);
    }
}
