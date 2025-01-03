package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.NotiFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.dto.response.NotiResult;
import com.moment.moment_BE.service.NotiService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiController {

    SimpMessagingTemplate messagingTemplate;

    NotiService notiService;


    @MessageMapping("/noti")
    public void sendMessage(String chatMessage) {

        messagingTemplate.convertAndSendToUser("duongngocle4231", "/topic/noti", chatMessage);
        System.out.println("Sending message to: " + "duongngocle4231");
    }

//    @SubscribeMapping("/user/{username}/topic/noti")
//    public void handleUserSubscription(@DestinationVariable String username, SimpMessageHeaderAccessor headerAccessor) {
//        System.out.println("start get username >>>");
//        // Lấy username từ sessionAttributes
//        String authenticatedUsername = (String) headerAccessor.getSessionAttributes().get("userName");
//
//        // Kiểm tra xem username trong URL có khớp với username đã xác thực không
//        if (authenticatedUsername == null || !authenticatedUsername.equals(username)) {
//            throw new SecurityException("User is not authorized to subscribe to this topic");
//        }
//
//        // Nếu username hợp lệ, cho phép subscribe
//        System.out.println("User " + authenticatedUsername + " subscribed to /user/" + username + "/topic/noti");
//    }

//    @MessageMapping("/sendMessage")
//    public void sendMessage(String message, SimpMessageHeaderAccessor headerAccessor) {
//        String authenticatedUsername = (String) headerAccessor.getSessionAttributes().get("userName");
//
//        // Handle sending messages
//        System.out.println("User " + authenticatedUsername + " sent message: " + message);
//    }

    @PostMapping("/noti/get")
    public ApiResponse<List<NotiResponse>> getNoti(@RequestBody @Valid NotiFilterRequest notiFilterRequest) {

        NotiResult notiResult = notiService.getNoti(notiFilterRequest);


        return ApiResponse.<List<NotiResponse>>builder()
                .result(notiResult.getNotiResponseList())
                .currentPage(notiFilterRequest.getPageCurrent())
                .totalItems(notiResult.getCountNoti())
                .build();
    }


}
