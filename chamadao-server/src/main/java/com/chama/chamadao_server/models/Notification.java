package com.chama.chamadao_server.models;

import com.chama.chamadao_server.models.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverWalletAddress;
    private String senderWalletAddress;
    private String chamaWalletAddress;
    private String message;
    private String chamaName;
    private NotificationType type;

    private boolean read;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
