package org.cft.mapper;

import org.cft.dto.CriticDto;
import org.cft.entity.Critic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CriticMapper {

    @Mapping(target = "user.id", source = "userId")
    Critic toEntity(CriticDto criticDto);
}
