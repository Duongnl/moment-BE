package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    UserResponse toUserResponse(Account account);
}
