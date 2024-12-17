package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.FriendResponse;
import com.moment.moment_BE.service.FriendService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendController {
   FriendService friendService;

   @GetMapping()
   public ApiResponse<List<FriendResponse>> getFriends() {
       List<FriendResponse> profileFriend = friendService.getListFriends(1);

       return ApiResponse.<List<FriendResponse>>builder()
               .result(profileFriend)
               .build();
   }
    @PostMapping("/add")
    public ApiResponse<?> addFriend(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {

       friendService.addFriend(friendInviteRequest,1);

        return ApiResponse.builder().build();
    }

    @PostMapping("/status")
    public ApiResponse<?> addFriendStatus(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {

        friendService.changeStatusFriend(friendInviteRequest,1);

        return ApiResponse.builder().build();
    }
}
