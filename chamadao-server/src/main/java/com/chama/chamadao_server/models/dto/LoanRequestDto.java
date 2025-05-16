package com.chama.chamadao_server.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
    private String chamaAddress;
    private String borrowerWalletAddress;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private String term;
    private Integer requiredGuarantors;
    private BigDecimal penalty;
    private Integer penaltyPeriod;
}
