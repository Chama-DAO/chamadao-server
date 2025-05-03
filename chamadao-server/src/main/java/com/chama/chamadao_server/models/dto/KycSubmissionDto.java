package com.chama.chamadao_server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycSubmissionDto {
    private String walletAddress;
    private String documentType; // NATIONAL_ID, PASSPORT, etc.
    private String documentNumber;
    private byte[] documentFile; // Binary file data or a secure reference
}

