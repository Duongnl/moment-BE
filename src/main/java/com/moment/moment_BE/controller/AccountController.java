package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.ChangePasswordRequest;
import com.moment.moment_BE.dto.request.RegisterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.dto.request.AccountInfoRequest;
import com.moment.moment_BE.entity.Friend;
import com.moment.moment_BE.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<AccountResponse> getAccountInfo() {
        // Lấy thông tin tài khoản của người dùng hiện tại từ service
        AccountResponse accountResponse = accountService.getAccountInfo();
        // Trả về thông tin tài khoản dưới dạng ApiResponse
        return ApiResponse.<AccountResponse>builder()
                .result(accountResponse)
                .build();
    }


    @PutMapping("/setting")
    public ApiResponse<Void> updateAccountInfo(@RequestBody AccountInfoRequest updateRequest) {
        // Lấy tên người dùng hiện tại từ ngữ cảnh bảo mật
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        // Cập nhật thông tin tài khoản
        accountService.updateAccountInfo(userName, updateRequest);
        // Trả về phản hồi thành công
        return ApiResponse.<Void>builder()
                .message("Account information updated successfully.")
                .build();
    }

    @PutMapping("change-username")
    public ApiResponse<Void> changeUserName(@RequestBody AccountInfoRequest updateRequest) {
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        accountService.changUserName(userName, updateRequest.getUserName());
        return ApiResponse.<Void>builder()
                .message("UserName updated successfully.").build();
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        // Lấy tên người dùng hiện tại từ ngữ cảnh bảo mật
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();

        // Gọi hàm thay đổi mật khẩu trong service
        accountService.changePassword(userName, request);

        // Trả về phản hồi thành công
        return ApiResponse.<Void>builder()
                .message("Password changed successfully.")
                .build();
    }

}
