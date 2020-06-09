package org.cft.mapper;

import org.cft.dto.UserDto;
import org.cft.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
}
