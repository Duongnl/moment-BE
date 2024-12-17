package com.moment.moment_BE.dto.request;

import com.moment.moment_BE.enums.FriendStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendInviteRequest {
    @NotBlank(message = "NOT_BLANK")
    String accountFriendId;

    FriendStatus status;
}