package com.moment.moment_BE.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moment.moment_BE.dto.request.PhotoFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.PhotoResponse;
import com.moment.moment_BE.service.PhotoService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhotoController {

     PhotoService photoService;


    @PostMapping("")
    public ApiResponse<List<PhotoResponse>> getListPhotoFriends(@RequestBody PhotoFilterRequest photoFilterRequest) {
        List<PhotoResponse> photos = photoService.getListPhotoMyFriends(
                photoFilterRequest,1
        );
        return ApiResponse.<List<PhotoResponse>>builder()
                .result(photos)
                .currentPage(photoFilterRequest.getPageCurrent())
                .build();
    }



}
