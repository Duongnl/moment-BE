package com.moment.moment_BE.service;

import com.google.firebase.messaging.*;
import com.moment.moment_BE.dto.FcmTokenRequest;
import com.moment.moment_BE.dto.PushRequest;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.FcmTokenEntity;
import com.moment.moment_BE.repository.AccountRepository;
import com.moment.moment_BE.repository.TokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    AccountRepository accountRepository;
    TokenRepository tokenRepository;

    @Transactional
    public boolean sendPushNotification(PushRequest request) throws FirebaseMessagingException {
        List<FcmTokenEntity> tokens = tokenRepository.findAllByAccountId(request.getAccountId());

        if (tokens.isEmpty()) {
            return false;
        }

        for (FcmTokenEntity token : tokens) {
            System.out.println("Gửi vào token: " + token.getToken());
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
    
    public boolean isSavedToken(FcmTokenRequest request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());

        if (accountOpt.isEmpty()) {
            return false;
        }

        Account account = accountOpt.get();

        Optional<FcmTokenEntity> existing = tokenRepository.findByToken(request.getToken());

        if (existing.isEmpty()) {
            tokenRepository.save(FcmTokenEntity.builder().token(request.getToken()).account(account).build());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean sendPushNotificationAll(PushRequest request) throws FirebaseMessagingException {
        List<FcmTokenEntity> tokens = tokenRepository.findAll();

        if (tokens.isEmpty()) {
            return false;
        }

        for (FcmTokenEntity token : tokens) {
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
    public boolean isDeleteByToken(FcmTokenRequest request){
        tokenRepository.deleteById(request.getToken());
        return true;
    }

}
