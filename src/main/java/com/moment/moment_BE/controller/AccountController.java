package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
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



}
