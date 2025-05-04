package com.chama.chamadao_server.models;

import com.chama.chamadao_server.models.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "kyc_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userWalletAddress;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentPath;
    private String documentHash;
    private boolean verified = false;
    private LocalDate uploadedAt;
    private LocalDate verifiedAt;

    @ManyToOne
    @JoinColumn(name = "walletAddress")
    private User user;
}