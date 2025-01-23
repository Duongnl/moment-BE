package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.NotiFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.NotiResponse;
import com.moment.moment_BE.dto.response.NumberOfNotiResponse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiController {

    NotiService notiService;

    @PostMapping("/get")
    public ApiResponse<List<NotiResponse>> getNoti(@RequestBody @Valid NotiFilterRequest notiFilterRequest) {
        System.out.println("noti filter request: " + notiFilterRequest);
        return ApiResponse.<List<NotiResponse>>builder()
                .result(notiService.getNoti(notiFilterRequest))
                .currentPage(notiFilterRequest.getPageCurrent())
                .build();
    }

//    @GetMapping("/count-noti-new")
//    public ApiResponse<Integer> countNotiNew(@RequestParam String time) {
//
//        return ApiResponse.<Integer>builder()
//                .totalItems(notiService.countNotiNew(time))
//                .build();
//    }
//
//
//    @GetMapping("/count-noti-all")
//    public ApiResponse<Integer> countNotiAll(@RequestParam String time) {
//
//        return ApiResponse.<Integer>builder()
//                .totalItems(notiService.countNotiAll(time))
//                .build();
//    }
//
//    @GetMapping("/count-noti-unread")
//    public ApiResponse<Integer> countNotiUnread(@RequestParam String time) {
//
//        return ApiResponse.<Integer>builder()
//                .totalItems(notiService.countNotiUnread(time))
//                .build();
//    }


    @GetMapping("/count-noti")
    public ApiResponse<NumberOfNotiResponse> countNoti(@RequestParam LocalDateTime time) {

        return ApiResponse.<NumberOfNotiResponse>builder()
                .result(notiService.countNoti(time))
                .build();
    }



}
