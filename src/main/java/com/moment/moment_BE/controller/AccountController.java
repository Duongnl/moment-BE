package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.AccountInfo;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/setting")
    public ResponseEntity<AccountResponse> getAccountInfo() {
        // Lấy thông tin tài khoản của người dùng hiện tại từ service
        AccountResponse accountResponse = accountService.getAccountInfo();

        // Trả về thông tin tài khoản dưới dạng response
        return ResponseEntity.ok(accountResponse);
    }



}
