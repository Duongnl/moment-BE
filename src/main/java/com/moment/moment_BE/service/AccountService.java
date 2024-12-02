package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.GlobalErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    ProfileMapper profileMapper;


    public List<Account> getAll() {
        return accountRepository.findAll();
    }


    public UserResponse getMyInfo () {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        Account account = accountRepository.findByUserName(name).orElseThrow(
                () -> new AppException(GlobalErrorCode.USER_NOT_FOUND)
        );

        UserResponse userResponse = new UserResponse();
        userResponse = accountMapper.toUserResponse(account);
        profileMapper.toUserResponse(userResponse,account.getProfile());

        return userResponse;
    }
}
