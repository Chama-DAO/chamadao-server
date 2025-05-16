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
import java.util.HashSet;
import java.util.Set;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chama_address")
    private Chama chama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_wallet_address", referencedColumnName = "walletAddress")
    private User borrower;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<LoanGuarantor> guarantors = new HashSet<>();
    // Loan details
    private BigDecimal loanAmount;
    private BigDecimal loanInterestRate;
    private String loanTerm;
    private LocalDateTime dueDate;
    private Integer requiredGuarantors;
    private BigDecimal totalGuaranteedAmount;

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

    //helper methods

    public void addGuarantor(User guarantor, BigDecimal amount) {
        LoanGuarantor loanGuarantor = LoanGuarantor.builder()
                .loan(this)
                .guarantor(guarantor)
                .guaranteedAmount(amount)
                .status(LoanGuarantor.GuarantorStatus.PENDING)
                .build();

        guarantors.add(loanGuarantor);
        updateTotalGuaranteedAmount();
    }

    public void updateTotalGuaranteedAmount() {
        this.totalGuaranteedAmount = guarantors.stream()
                .filter(g -> g.getStatus() == LoanGuarantor.GuarantorStatus.APPROVED)
                .map(LoanGuarantor::getGuaranteedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}