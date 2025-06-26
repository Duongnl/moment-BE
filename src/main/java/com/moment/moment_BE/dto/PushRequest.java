package com.moment.moment_BE.dto;

import lombok.Data;

@Data
public class PushRequest {
    private String accountId;
    private String title;
    private String body;
    private String url = "/";
}
