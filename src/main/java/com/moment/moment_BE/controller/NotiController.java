package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.NotiFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.service.NotiService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
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

    @PostMapping("/noti/get")
    public ApiResponse<List<NotiResponse>> getNoti(@RequestBody @Valid NotiFilterRequest notiFilterRequest) {




        return ApiResponse.<List<NotiResponse>>builder()
                .result(notiService.getNoti(notiFilterRequest))
                .currentPage(notiFilterRequest.getPageCurrent())
                .totalItems(notiService.countNoti(notiFilterRequest))
                .build();
    }


}
