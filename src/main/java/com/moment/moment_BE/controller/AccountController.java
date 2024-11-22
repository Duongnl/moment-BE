package com.moment.moment_BE.controller;

import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping()
    public  Set<Friend> getAll() {
        List<Account> accounts = accountService.getAll();
        Set<Friend> friends = accounts.get(2).getFriends();
        friends.forEach(f -> {
            System.out.println(f.getFriend().getUserName());

        });
        return friends;
    }

}
