package com.chama.chamadao_server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simplified version of ChamaDto for summary views to avoid circular references
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamaSummaryDto {
    private String chamaAddress;
    private String chamaId;
    private String name;
    private String description;
    private String profileImage;
    // Include only essential information
}