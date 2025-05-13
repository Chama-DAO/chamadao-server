package com.chama.chamadao_server.mappers;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.ChamaDto;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Chama entities and ChamaDto objects
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChamaMapper {

    /**
     * Convert a Chama entity to a ChamaDto
     * @param chama The Chama entity to convert
     * @return The converted ChamaDto
     */
    // @Mapping(target = "creatorWalletAddress", expression = "java(chama.getCreator() != null ? chama.getCreator().getWalletAddress() : null)") // Commented out as per requirements
    @Mapping(target = "members", source = "members", qualifiedByName = "mapMembers")
    ChamaDto toDto(Chama chama);

    /**
     * Convert a ChamaDto to a Chama entity
     * @param chamaDto The ChamaDto to convert
     * @return The converted Chama entity
     */
   // @Mapping(target = "creator", ignore = true)
    @Mapping(target = "members", ignore = true)
    Chama toEntity(ChamaDto chamaDto);

    /**
     * Extract wallet addresses from a list of users
     * @param chama The Chama entity containing the members
     * @return A list of wallet addresses
     */
    @Named("mapMembers")
    default List<String> mapMembers(List<User> members) {
        if (members == null) {
            return List.of();
        }
        return members.stream()
                .map(User::getWalletAddress)
                .collect(Collectors.toList());
    }
}
