package com.chama.chamadao_server.mappers;

import com.chama.chamadao_server.models.Chama;
import com.chama.chamadao_server.models.User;
import com.chama.chamadao_server.models.dto.ChamaSummaryDto;
import com.chama.chamadao_server.models.dto.UserDto;
import com.chama.chamadao_server.models.dto.UserSummaryDto;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "createdChamas", qualifiedByName = "toChamaSummaryDtoSet")
    @Mapping(target = "memberChamas", qualifiedByName = "toChamaSummaryDtoSet")
    UserDto toDto(User user);

    @Mapping(target = "createdChamas", ignore = true)
    @Mapping(target = "memberChamas", ignore = true)
    User toEntity(UserDto userDto);

    UserSummaryDto toSummaryDto(User user);

    @Named("toChamaSummaryDtoSet")
    default Set<ChamaSummaryDto> toChamaSummaryDtoSet(Set<Chama> chamas) {
        if (chamas == null) {
            return new HashSet<>();
        }
        return chamas.stream()
                .map(chama -> ChamaSummaryDto.builder()
                        .chamaAddress(chama.getChamaAddress())
                        .chamaId(chama.getChamaId())
                        .name(chama.getName())
                        .description(chama.getDescription())
                        .profileImage(chama.getProfileImage())
                        .build())
                .collect(Collectors.toSet());
    }

    // Update existing User entity with values from DTO
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}