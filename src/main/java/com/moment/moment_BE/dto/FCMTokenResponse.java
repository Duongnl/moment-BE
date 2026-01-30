package com.moment.moment_BE.dto;

import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.entity.Account;
import lombok.Data;

@Data
public class FCMTokenResponse {
    private String token;
    private AccountResponse account;
    private String createdAt;
}
