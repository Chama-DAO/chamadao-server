package com.chama.chamadao_server.mappers;

import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.UserDto;
import com.chama.chamadao_server.models.enums.KycStatus;
import org.mapstruct.*;

import java.util.HashSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convert a User entity to a UserDto
     * @param user The User entity to convert
     * @return The converted UserDto
     */
    // KYC details commented out as per requirements (future feature)
    // @Mapping(target = "kycVerified", expression = "java(user.getKycStatus() == com.chama.chamadao_server.models.enums.KycStatus.VERIFIED)")
    @Mapping(target = "chamaWalletAddress", expression = "java(user.getChama() != null ? user.getChama().getWalletAddress() : null)")
    @Mapping(target = "chamaName", expression = "java(user.getChama() != null ? user.getChama().getName() : null)")
    UserDto toDto(User user);

    /**
     * Convert a UserDto to a User entity
     * @param userDto The UserDto to convert
     * @return The converted User entity
     */
    // KYC details commented out as per requirements (future feature)
    // @Mapping(target = "kycStatus", expression = "java(userDto.isKycVerified() ? com.chama.chamadao_server.models.enums.KycStatus.VERIFIED : com.chama.chamadao_server.models.enums.KycStatus.PENDING)")
    @Mapping(target = "reputationScore", ignore = true)
    @Mapping(target = "chama", ignore = true) // Chama relationship is managed by ChamaService
    User toEntity(UserDto userDto);

//    @AfterMapping
//    default void setDefaultValues(@MappingTarget User user, UserDto userDto) {
//        // Only set these if they're provided in the DTO
//        if (userDto.getRoles() == null) {
//            user.setRoles(new HashSet<>());
//        }
//    }
}
