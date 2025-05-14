package com.chama.chamadao_server.mappers;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.ChamaDto;
import com.chama.chamadao_server.models.dto.ChamaSummaryDto;
import com.chama.chamadao_server.models.dto.UserSummaryDto;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface ChamaMapper {

    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "members", qualifiedByName = "mapMembers")
    ChamaDto toDto(Chama chama);

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "members", ignore = true)
    Chama toEntity(ChamaDto chamaDto);

    ChamaSummaryDto toSummaryDto(Chama chama);

    @Named("mapMembers")
    default Set<UserSummaryDto> mapMembers(Set<User> members) {
        if (members == null) {
            return new HashSet<>();
        }
        return members.stream()
                .map(user -> UserSummaryDto.builder()
                        .walletAddress(user.getWalletAddress())
                        .fullName(user.getFullName())
                        .profileImage(user.getProfileImage())
                        .build())
                .collect(Collectors.toSet());
    }

    // Update existing Chama entity with values from DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChamaFromDto(ChamaDto chamaDto, @MappingTarget Chama chama);
}