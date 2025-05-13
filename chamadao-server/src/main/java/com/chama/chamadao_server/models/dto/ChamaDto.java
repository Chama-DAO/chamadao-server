package com.chama.chamadao_server.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChamaDto {
    private String chamaAddress;
    private String chamaId;
    private String name;
    private String description;
    private String location;
    private String profileImage;
    
    // Creator is just the wallet address
    private String creator;
    
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
    
    // Just wallet addresses of members
    private List<String> members = new ArrayList<>();
    // private List<LoanDto> loans = new ArrayList<>();

    private LocalDateTime dateCreated;
    private LocalDateTime updatedAt;
}