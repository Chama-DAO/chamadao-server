package com.chama.chamadao_server.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Chama {
    @Id
    private String walletAddress;

    private String name;

    private String description;

    // Creator of the Chama - commented out as per requirements
    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "creator_wallet_address", referencedColumnName = "walletAddress")
    // private User creator;

    // List of chama members
    @JsonManagedReference
    @OneToMany(mappedBy = "chama", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<User> members = new ArrayList<>();
    private Long contributionAmount;
    private Long contributionPeriod;
    private int maximumMembers;
    private Long maximumLoanAmount;
    private int loanPaymentPeriod;
    private Long loanPenaltyAmount;
    private Long registrationFee;
    private String chamaProfileImageUrl;

    // Timestamps
    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    // Helper method to add a member
    public void addMember(User user) {
        members.add(user);
        user.setChama(this);
    }

    // Helper method to remove a member
    public void removeMember(User user) {
        members.remove(user);
        user.setChama(null);
    }
}
