package com.moment.moment_BE.service;

import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }
}
