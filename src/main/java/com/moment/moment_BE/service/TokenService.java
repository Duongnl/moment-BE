package com.moment.moment_BE.service;

import com.google.firebase.messaging.*;
import com.moment.moment_BE.dto.FCMTokenRequest;
import com.moment.moment_BE.dto.FCMTokenResponse;
import com.moment.moment_BE.dto.PushRequest;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.FcmToken;
import com.moment.moment_BE.exception.AccountErrorCode;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.mapper.FCMTokenMapper;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.TokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    AccountRepository accountRepository;
    TokenRepository tokenRepository;
    NotiPushService notiPushService;
    FCMTokenMapper fcmTokenMapper;

    public boolean sendPushNotification(PushRequest request) throws FirebaseMessagingException {
        List<FcmToken> tokens = tokenRepository.findAllByAccountId(request.getAccountId());

        if (tokens.isEmpty()) {
            return false;
        }

        //        for (FcmToken token : tokens) {
//            System.out.println("Gửi vào token: " + token.getToken());
//            try {
//                Message message = Message.builder()
//                        .setToken(token.getToken())
//                        .putData("title", request.getTitle())
//                        .putData("body", request.getBody())
//                        .putData("url", request.getUrl())
//                        .build();
//                FirebaseMessaging.getInstance().send(message);
//                success = true;
//            } catch (FirebaseMessagingException ex) {
//                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
//                    // ✅ Token không còn hợp lệ → xóa khỏi DB
//                    tokenRepository.delete(token);
//                } else {
//                    throw ex;
//                }
//            }
//        }
        return notiPushService.sendPushNotiPerAccount(request.getAccountId(), request.getTitle(), request.getBody(), request.getUrl());
    }

    public FCMTokenResponse savedToken(FCMTokenRequest request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());

        if (accountOpt.isEmpty()) throw new AppException(AccountErrorCode.USER_NOT_FOUND);

        Account account = accountOpt.get();

        Optional<FcmToken> existing = tokenRepository.findByToken(request.getToken());

        if (existing.isEmpty()) {
            FcmToken tokenEntity = tokenRepository.save(FcmToken.builder()
                    .token(request.getToken())
                    .account(account)
                    .createdAt(LocalDateTime.now())
                    .build()
            );
            System.out.println("da luu token thanh cong cho: " + account.getProfile().getName());
            // Gửi thông báo bên ngoài transaction
            try {
                if (notiPushService.sendPushNotiPerAccount(
                        request.getAccountId(),
                        "Thông báo",
                        "Đăng ký nhận thông báo thành công",
                        "/"
                ))
                    System.out.println("Gui thong bao thanh cong");
            } catch (Exception e) {
                System.err.println("Lỗi khi gửi thông báo: " + e.getMessage());
            }
            return fcmTokenMapper.toFCMTokenResponse(tokenEntity);
        }
        return fcmTokenMapper.toFCMTokenResponse(existing.get());

    }

    @Transactional
    public boolean sendPushNotificationAll(PushRequest request) throws FirebaseMessagingException {
        List<FcmToken> tokens = tokenRepository.findAll();

        if (tokens.isEmpty()) {
            return false;
        }

        for (FcmToken token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .putData("title", request.getTitle())
                        .putData("body", request.getBody())
                        .putData("url", request.getUrl())
                        .build();
                FirebaseMessaging.getInstance().send(message);
                return true;
            } catch (FirebaseMessagingException ex) {
                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    // ✅ Token không còn hợp lệ → xóa khỏi DB
                    tokenRepository.delete(token);
                } else {
                    throw ex;
                }
            }
        }
        return false;
    }

    @Transactional
    public boolean isDeleteByToken(FCMTokenRequest request) {
        tokenRepository.deleteByToken(request.getToken());
        return true;
    }

}
