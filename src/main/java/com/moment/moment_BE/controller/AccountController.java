package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.FriendInviteRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.enums.FriendStatus;
import com.moment.moment_BE.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {

     AccountService accountService;


    @GetMapping()
    public  List<Account> getAll() {
//        lay ra all account
        List<Account> accounts = accountService.getAll();

//        list
        Set<Friend> friends = accounts.get(1).getFriends();

        friends.forEach(f -> {
            System.out.println(f.getAccountFriend().getProfile());

        });

        return accountService.getAll();
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return  ApiResponse.<AuthenticationResponse>builder()
                .result(accountService.register(registerRequest))
                .build();
    }

    @GetMapping("/friend")
    public ApiResponse<List<AccountResponse>> getFriends() {
        List<AccountResponse> profileFriend = accountService.getListAccountFriend(1, FriendStatus.accepted);

        return ApiResponse.<List<AccountResponse>>builder()
                .result(profileFriend)
                .build();
    }
    @GetMapping("/friend/invited")
    public ApiResponse<List<AccountResponse>> getFriendsInvited() {
        List<AccountResponse> profileFriend = accountService.getListAccountFriend(1,FriendStatus.invited);

        return ApiResponse.<List<AccountResponse>>builder()
                .result(profileFriend)
                .build();
    }
    @GetMapping("/friend/sent")
    public ApiResponse<List<AccountResponse>> getFriendsSent() {
        List<AccountResponse> profileFriend = accountService.getListAccountFriend(1,FriendStatus.sent);

        return ApiResponse.<List<AccountResponse>>builder()
                .result(profileFriend)
                .build();
    }

    @PostMapping("/friend/add")
    public ApiResponse<?> addFriend(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {

       AccountResponse friendResponse=accountService.addFriend(friendInviteRequest,1);

        return ApiResponse.builder().result(friendResponse).build();
    }


    @PostMapping("friend/status")
    public ApiResponse<?> changeFriendStatus(@RequestBody @Valid FriendInviteRequest friendInviteRequest) {

        AccountResponse friendResponse =accountService.changeStatusFriend(friendInviteRequest,1);

        return ApiResponse.builder().result(friendResponse).build();
    }

    @GetMapping("/search")
    public ApiResponse<?> searchFriend( @RequestParam String s) {

        List<AccountResponse> friendResponse=accountService.searchAccount(s,1);

        return ApiResponse.builder().result(friendResponse).build();
    }


}
