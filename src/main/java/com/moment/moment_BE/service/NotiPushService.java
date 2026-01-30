package com.moment.moment_BE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.moment.moment_BE.entity.FcmToken;
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

    public boolean sendPushNotiPerAccount(String accountReceiveId, String title, String body, String url) {
        List<FcmToken> tokens = tokenRepository.findAllByAccountId(accountReceiveId);

        for (FcmToken token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .putData("title", title)
                        .putData("body", body)
                        .putData("url", url)
                        .build();

                FirebaseMessaging.getInstance().send(message);

            } catch (FirebaseMessagingException ex) {
                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    try {
//                        tokenRepository.deleteById(token.getId());
                    } catch (Exception e) {
                        System.out.println("Xóa token lỗi: " + token.getId() + " - " + e.getMessage());
                    }
                } else {
                    System.out.println("Lỗi FCM: " + ex.getMessage());
                }
                return false;
            }
        }
        return true;
    }

}
