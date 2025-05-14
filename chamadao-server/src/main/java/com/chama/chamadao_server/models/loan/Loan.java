package com.chama.chamadao_server.models.loan;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "chama_address")
    private Chama chama;

    @ManyToOne
    @JoinColumn(name = "borrower_wallet_address", referencedColumnName = "walletAddress")
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "guarantor_wallet_address")
    private User guarantor;
    // Loan details
    private BigDecimal loanAmount;
    private BigDecimal loanInterestRate;
    private String loanTerm;
    private LocalDateTime dueDate;

    // Status tracking
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    // Penalty information
    private BigDecimal loanPenalty;
    private Integer loanPenaltyExpirationPeriod;

    // Dates
    @CreationTimestamp
    private LocalDateTime dateIssued;

    @LastModifiedDate
    private LocalDateTime lastUpdated;

    // Amount tracking
    private BigDecimal amountRepaid;
    private BigDecimal outstandingAmount;
}