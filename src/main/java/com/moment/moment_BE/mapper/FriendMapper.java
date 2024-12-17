package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.response.FriendResponse;
import com.moment.moment_BE.entity.Friend;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendMapper {
    Friend toFriend(FriendResponse friendResponse);

    FriendResponse toFriendResponse(Friend friend);
}
