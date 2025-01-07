package com.moment.moment_BE.service;


import com.moment.moment_BE.dto.request.AuthenticationRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.AuthErrorCode;
import com.moment.moment_BE.mapper.AccountMapper;
import com.moment.moment_BE.mapper.ProfileMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    ProfileMapper profileMapper;
    AuthenticationService authenticationService;
    ProfileRepository profileRepository;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    //    login ne
    public AuthenticationResponse register(RegisterRequest request) {
        if (accountRepository.existsByUserName(request.getUserName())) {
            throw new AppException(AccountErrorCode.USER_NAME_EXISTED);
        }

//        khoi tao gan du lieu
        Account account = accountMapper.toAccount(request);
        Profile profile = profileMapper.toProfile(request);

        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(1);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        account.setProfile(profile);
        profile.setAccount(account);

//        save
        try {
            profileRepository.save(profile);
        } catch (Exception e) {
           throw  new AppException(AccountErrorCode.SAVE_USER_FAIL);
        }

//        tao token khi save thanh cong
        var token = authenticationService.generateToken(account);

        return
                AuthenticationResponse.builder()
                                .token(token)
                                .authenticated(true)
                                .build();

    }

    public AccountResponse getAccountInfo() {
        // Lấy tên người dùng hiện tại từ ngữ cảnh bảo mật
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();

        // Lấy thông tin Profile dựa trên userName hiện tại
        Profile profile = profileRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Xây dựng phản hồi AccountResponse từ Profile
        return AccountResponse.builder()
                .name(profile.getName())
                .email(profile.getAccount().getEmail())
                .userName(profile.getAccount().getUserName())
                .birthday(profile.getBirthday())
                .sex(profile.getSex())
                .phoneNumber(profile.getAccount().getPhoneNumber())
                .address(profile.getAddress())
                .build();
    }






}
