package com.chama.chamadao_server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chamas")
public class Chama {
    @Id
    private String chamaAddress;
    
    private String chamaId;
    private String name;
    private String description;
    private String location;
    private String profileImage;
    
    // Store creator as string wallet address
    private String creatorAddress;
    
    private Integer maximumMembers;
    private Boolean registrationFeeRequired;
    private BigDecimal registrationFeeAmount;
    private String registrationFeeCurrency;
    private String payoutPeriod;
    private Integer payoutPercentageAmount;
    
    private BigDecimal contributionAmount;
    private String contributionPeriod; 
    private BigDecimal contributionPenalty;
    private Integer penaltyExpirationPeriod;
    
    private BigDecimal maximumLoanAmount;
    private BigDecimal loanInterestRate;
    private String loanTerm;
    private BigDecimal loanPenalty;
    private Integer loanPenaltyExpirationPeriod;
    private Integer minContributionRatio;
    
    private BigDecimal totalContributions;
    private BigDecimal totalPayouts;
    private BigDecimal totalLoans;
    private BigDecimal totalLoanRepayments;
    private BigDecimal totalLoanPenalties;
    
    // @OneToMany(mappedBy = "chama", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Loan> loans = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "chama_members",
        joinColumns = @JoinColumn(name = "chama_wallet_address"),
        inverseJoinColumns = @JoinColumn(name = "member_wallet_address")
    )
    private List<User> members = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime dateCreated;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Helper methods
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            user.getMemberChamas().add(this);
        }
    }
    
    public void removeMember(User user) {
        if (members.contains(user)) {
            members.remove(user);
            user.getMemberChamas().remove(this);
        }
    }
}