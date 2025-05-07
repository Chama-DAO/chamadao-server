package com.chama.chamadao_server.models.dto;

import com.chama.chamadao_server.models.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String walletAddress;
    private String fullName;
    private String mobileNumber;
    private String email;
    // KYC details commented out as per requirements (future feature)
    // private boolean kycVerified;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Reference to the Chama the user belongs to
    private String chamaWalletAddress;
    private String chamaName;
}
