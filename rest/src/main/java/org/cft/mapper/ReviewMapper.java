package org.cft.mapper;

import org.cft.dto.ReviewDto;
import org.cft.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "author.id", source = "authorId")
    Review toEntity(ReviewDto reviewDto);
}
