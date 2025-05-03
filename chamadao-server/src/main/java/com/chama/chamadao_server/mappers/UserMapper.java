package com.chama.chamadao_server.mappers;


import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserDto toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDto.builder()
                .id(user.getId())
                .walletAddress(user.getWalletAddress())
                .fullName(user.getFullName())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .kycVerified(user.isKycVerified())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}