package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    void toUserResponse(@MappingTarget UserResponse userResponse, Profile profile);
}
