package com.moment.moment_BE.service;


import com.moment.moment_BE.dto.request.ChangePasswordRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.dto.request.AccountInfoRequest;
import com.moment.moment_BE.entity.Profile;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
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

    public void updateAccountInfo(String userName, AccountInfoRequest updateRequest) {
        // Kiểm tra sự tồn tại của Profile
        Profile profile = profileRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Cập nhật thông tin Profile
        profileRepository.updateProfileByUserName(
                updateRequest.getName(),
                updateRequest.getBirthday(),
                updateRequest.getSex(),
                updateRequest.getAddress(),
                userName
        );
    }

    public void changUserName(String currentUserName, String newUserName) {
        if (accountRepository.existsByUserName(newUserName)) {
            throw new AppException(AccountErrorCode.USER_NAME_EXISTED);
        }
        Account account = accountRepository.findByUserNameAndStatus(currentUserName, 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));
        account.setUserName(newUserName);
        accountRepository.save(account);
    }

    public void changePassword(String userName, ChangePasswordRequest request) {
        // Tìm tài khoản dựa trên tên người dùng
        Account account = accountRepository.findByUserNameAndStatus(userName, 1)
                .orElseThrow(() -> new AppException(AccountErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new AppException(AccountErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // Kiểm tra nếu mật khẩu cũ và mật khẩu mới giống nhau
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(AccountErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        // Cập nhật mật khẩu mới
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Lưu tài khoản vào cơ sở dữ liệu
        try {
            accountRepository.save(account);
        } catch (Exception e) {
            throw new AppException(AccountErrorCode.SAVE_PASSWORD_FAIL);
        }
    }







}
