package com.moment.moment_BE.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.moment.moment_BE.dto.FcmTokenRequest;
import com.moment.moment_BE.dto.PushRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.service.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationPushController {

    TokenService tokenService;

    @PostMapping("/save-token")
    public ResponseEntity<String> saveToken(@RequestBody FcmTokenRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Token không hợp lệ");
        }
        if (tokenService.isSavedToken(request))

            return ResponseEntity.ok("Đã lưu token");
        else return ResponseEntity.ok("Lưu token thất bại");
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
    public ApiResponse<String> deleteToken(@RequestBody FcmTokenRequest request) {
        if (tokenService.isDeleteByToken(request))
        return ApiResponse.<String>builder()
                .result("Xóa thành công")
                .build();
        else return ApiResponse.<String>builder()
                .result("Xóa token không thành công")
                .build();
    }

}
