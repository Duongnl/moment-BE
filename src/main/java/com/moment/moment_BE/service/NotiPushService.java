package com.moment.moment_BE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.FcmTokenEntity;
import com.moment.moment_BE.repository.TokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// kh khai bao gi het thi no autowired va private
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiPushService {
    TokenRepository tokenRepository;

    public void sendPushNotiPerAccount(Account account,String title,String body,String url) {
        List<FcmTokenEntity> tokens = tokenRepository.findAllByAccountId(account.getId());
        for (FcmTokenEntity token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .putData("title", title)
                        .putData("body", body)
                        .putData("url", url)
                        .build();
                FirebaseMessaging.getInstance().send(message);
                return;
            } catch (FirebaseMessagingException ex) {
                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    tokenRepository.delete(token);
                } else {
                    System.out.println(ex);
                }
            }
        }
    }
}
