package com.moment.moment_BE.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.moment.moment_BE.dto.FCMTokenRequest;
import com.moment.moment_BE.dto.FCMTokenResponse;
import com.moment.moment_BE.dto.PushRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.entity.FcmToken;
import com.moment.moment_BE.service.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationPushController {

    TokenService tokenService;

    @PostMapping("/save-token")
    public ApiResponse<FCMTokenResponse> saveToken(@RequestBody FCMTokenRequest request) {
        return ApiResponse.<FCMTokenResponse>builder()
                .result(tokenService.savedToken(request))
                .build();
    }

    @PostMapping("/send")
    public ApiResponse<String> sendNotification(@RequestBody PushRequest request) throws FirebaseMessagingException {
        if (tokenService.sendPushNotification(request))
            return ApiResponse.<String>builder()
                    .result("Gửi thông báo thành công")
                    .build();
        return ApiResponse.<String>builder()
                .result("Gửi thông báo thất bại")
                .build();

    }

    @PostMapping("/send-all")
    public ApiResponse<String> sendNotificationAll(@RequestBody PushRequest request) throws FirebaseMessagingException {
        if (tokenService.sendPushNotificationAll(request))
            return ApiResponse.<String>builder()
                    .result("Gửi thông báo thành công")
                    .build();
        return ApiResponse.<String>builder()
                .result("Gửi thông báo thất bại")
                .build();
    }

    @PostMapping("/delete")
    public ApiResponse<String> deleteToken(@RequestBody FCMTokenRequest request) {
        if (tokenService.isDeleteByToken(request))
        return ApiResponse.<String>builder()
                .result("Xóa thành công")
                .status(200)
                .build();
        else return ApiResponse.<String>builder()
                .result("Xóa token không thành công")
                .status(400)
                .build();
    }

}
