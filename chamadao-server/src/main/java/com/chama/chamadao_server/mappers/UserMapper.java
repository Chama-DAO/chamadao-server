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
    UserDto toDto(User user);

    /**
     * Convert a UserDto to a User entity
     * @param userDto The UserDto to convert
     * @return The converted User entity
     */
    @Mapping(target = "reputationScore", ignore = true)
    User toEntity(UserDto userDto);
}
