package com.moment.moment_BE.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword; // Mật khẩu cũ
    private String newPassword; // Mật khẩu mới
}
