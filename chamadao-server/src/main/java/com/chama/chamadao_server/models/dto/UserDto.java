package com.chama.chamadao_server.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String walletAddress;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String country;
    private String idNumber;
    // KYC details commented out as per requirements (future feature)
    // private boolean kycVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    //Reference to the Chama the user belongs to
    private String chamaWalletAddress;
    private String chamaName;

}
