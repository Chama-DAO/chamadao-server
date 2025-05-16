package com.chama.chamadao_server.models.loan;

import com.chama.chamadao_server.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_guarantors")
public class LoanGuarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantor_wallet_address", referencedColumnName = "walletAddress")
    private User guarantor;
    private BigDecimal guaranteedAmount;
    @Enumerated(EnumType.STRING)
    private GuarantorStatus status;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public enum GuarantorStatus {
        PENDING, APPROVED, REJECTED
    }
}
