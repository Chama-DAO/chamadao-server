package com.chama.chamadao_server.models.dto;

import com.chama.chamadao_server.models.User;
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
    private User creator;
    private List<String> memberWalletAddresses = new ArrayList<>();
    private Long contributionAmount;
    private Long contributionPeriod;
    private int maximumMembers;
    private Long maximumLoanAmount;
    private int loanPaymentPeriod;
    private Long loanPenaltyAmount;
    private String chamaProfileImageUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
