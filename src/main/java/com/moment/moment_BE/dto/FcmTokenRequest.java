package com.moment.moment_BE.dto;

import lombok.Data;

@Data
public class FcmTokenRequest {
    private String token;
    private String accountId;
}
