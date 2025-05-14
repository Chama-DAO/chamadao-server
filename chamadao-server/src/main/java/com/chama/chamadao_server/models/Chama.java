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
import java.util.*;

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

    // Reference to creator user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_address", referencedColumnName = "walletAddress")
    private User creator;

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
            joinColumns = @JoinColumn(name = "chama_address"),
            inverseJoinColumns = @JoinColumn(name = "wallet_address")
    )
    @Builder.Default
    private Set<User> members = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chama chama = (Chama) o;
        return Objects.equals(chamaAddress, chama.chamaAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chamaAddress);
    }
}