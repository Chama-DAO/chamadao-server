package com.chama.chamadao_server.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private String country;
    private String idNumber;
    private String profileImage;
    private Double reputationScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Use DTOs instead of entities for nested objects
    @Builder.Default
    private Set<ChamaSummaryDto> createdChamas = new HashSet<>();

    @Builder.Default
    private Set<ChamaSummaryDto> memberChamas = new HashSet<>();
}