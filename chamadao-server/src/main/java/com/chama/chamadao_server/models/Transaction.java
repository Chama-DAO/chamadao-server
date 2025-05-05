package com.chama.chamadao_server.models;

import com.chama.chamadao_server.models.enums.TransactionStatus;
import com.chama.chamadao_server.models.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a financial transaction in the system
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String walletAddress;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amountKES;

    @Column(nullable = false)
    private BigDecimal amountUSDT;

    private String mpesaReceiptNumber;

    private String blockchainTxHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
