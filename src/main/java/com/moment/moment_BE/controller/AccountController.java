package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.service.AccountService;
import com.moment.moment_BE.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<Account> accounts = accountService.getAll();
        Set<Friend> friends = accounts.get(2).getFriends();
        accounts.forEach(f -> {
            System.out.println(f.getProfile().getName());

        });
        return accountService.getAll();
    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo () {
        return ApiResponse.<UserResponse>builder()
                .result(accountService.getMyInfo())
                .build();
    }

}
