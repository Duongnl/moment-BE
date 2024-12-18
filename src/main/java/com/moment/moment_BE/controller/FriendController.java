package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
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

//    @PostMapping("/add")
//    public ApiResponse<?> addFriend(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {
//
//       FriendResponse friendResponse=friendService.addFriend(friendInviteRequest,1);
//
//        return ApiResponse.builder().result(friendResponse).build();
//    }
//
//    @PostMapping("/status")
//    public ApiResponse<?> addFriendStatus(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {
//
//        FriendResponse friendResponse =friendService.changeStatusFriend(friendInviteRequest,1);
//
//        return ApiResponse.builder().result(friendResponse).build();
//    }
//
//    @GetMapping("/find")
//    public ApiResponse<?> searchFriend( @RequestParam String valueFind) {
//
//        List<FriendResponse> friendResponse=friendService.searchFriend(valueFind,1);
//
//        return ApiResponse.builder().result(friendResponse).build();
//    }

}
