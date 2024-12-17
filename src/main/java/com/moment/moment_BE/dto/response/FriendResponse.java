package com.moment.moment_BE.dto.response;

import com.moment.moment_BE.enums.FriendStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendResponse {
    String name;

    String urlPhoto;

    String urlProfile;

    FriendStatus friendStatus;

    String requestedAt;

    String id;
}
