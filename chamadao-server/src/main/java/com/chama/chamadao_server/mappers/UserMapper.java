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
    @Mapping(target = "kycVerified", expression = "java(user.getKycStatus() == com.chama.chamadao_server.models.enums.KycStatus.VERIFIED)")
    UserDto toDto(User user);

    /**
     * Convert a UserDto to a User entity
     * @param userDto The UserDto to convert
     * @return The converted User entity
     */
    @Mapping(target = "kycStatus", expression = "java(userDto.isKycVerified() ? com.chama.chamadao_server.models.enums.KycStatus.VERIFIED : com.chama.chamadao_server.models.enums.KycStatus.PENDING)")
    @Mapping(target = "reputationScore", ignore = true)
    User toEntity(UserDto userDto);

    @AfterMapping
    default void setDefaultValues(@MappingTarget User user, UserDto userDto) {
        // Only set these if they're provided in the DTO
        if (userDto.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
    }
}
