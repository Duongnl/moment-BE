package com.moment.moment_BE.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.moment.moment_BE.dto.response.PhotoResponse;
import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    UserResponse toUserResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "profile.name", target="name")
//    @Mapping(source = "profile.url", target="urlAvt") map bang service
    void toPhotoResponse(@MappingTarget PhotoResponse photoResponse, Account account);
}
