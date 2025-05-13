package com.chama.chamadao_server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(unique = true, nullable = false, length = 42)
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "Invalid Ethereum wallet address format")
    private String walletAddress;

    private String fullName;
    //private String username;
    private String mobileNumber;
    @Email
    private String email;
    private String country;
    private String idNumber;
    private String profileImage;


    // Chamas created by this user
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Chama> createdChamas = new ArrayList<>();
    
    // Chamas where this user is a member
    @ManyToMany(mappedBy = "members")
    private List<Chama> memberChamas = new ArrayList<>();
    
    
    private Double reputationScore;
    //timestamps
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
