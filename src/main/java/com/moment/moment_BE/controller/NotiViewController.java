package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.NotiViewRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.service.NotiService;
import com.moment.moment_BE.service.NotiViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti-view")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotiViewController {

    NotiViewService notiViewService;

    @PostMapping
    public ApiResponse<?> saveNotiViews(@RequestBody NotiViewRequest notiViewRequest) {

        notiViewService.saveNotiViews(notiViewRequest);
        return ApiResponse.builder().build();
    }

}
