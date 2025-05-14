package com.chama.chamadao_server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(unique = true, nullable = false, length = 42)
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid Ethereum wallet address format")
    private String walletAddress;

    private String fullName;
    private String mobileNumber;

    @Email
    private String email;
    private String country;
    private String idNumber;
    private String profileImage;

    // Chamas created by this user
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Chama> createdChamas = new HashSet<>();

    // Chamas where this user is a member
    @ManyToMany(mappedBy = "members")
    @Builder.Default
    private Set<Chama> memberChamas = new HashSet<>();

    private Double reputationScore;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(walletAddress, user.walletAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletAddress);
    }
}