package com.chama.chamadao_server.models.dto;

import com.chama.chamadao_server.models.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {

    private Long id;
    private String chamaAddress;
    private String chamaName;
    private String borrowerWalletAddress;
    private String borrowerName;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private String term;
    private LocalDateTime dueDate;
    private LoanStatus status;
    private Integer requiredGuarantors;
    private BigDecimal totalGuaranteedAmount;
    private BigDecimal amountRepaid;
    private BigDecimal outstandingAmount;
}
