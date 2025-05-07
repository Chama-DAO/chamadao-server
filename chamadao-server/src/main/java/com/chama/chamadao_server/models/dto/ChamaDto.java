package com.chama.chamadao_server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Chama entities
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChamaDto {
    private String walletAddress;
    private String name;
    private String description;
    // private String creatorWalletAddress; // Commented out as per requirements
    private List<String> memberWalletAddresses = new ArrayList<>();
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
