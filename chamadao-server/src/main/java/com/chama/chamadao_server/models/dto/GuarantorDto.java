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
public class GuarantorDto {
    private String walletAddress;
    private String name;
    private BigDecimal guaranteedAmount;
    private String status;
}
