package com.moment.moment_BE.dto;

import lombok.Data;

@Data
public class FCMTokenRequest {
    private String token;
    private String accountId;
}
