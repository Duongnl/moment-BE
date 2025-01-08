package com.moment.moment_BE.controller;

import java.util.List;

import com.moment.moment_BE.dto.request.PostRequest;
import jakarta.validation.Valid;
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

import static com.moment.moment_BE.utils.DateTimeUtils.convertUtcToUserLocalTime;
import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhotoController {

    PhotoService photoService;


    @PostMapping("")
    public ApiResponse<List<PhotoResponse>> getListPhotoFriends(@RequestBody PhotoFilterRequest photoFilterRequest) {
        List<PhotoResponse> photos = photoService.getListPhotoMyFriends(
                photoFilterRequest, 1
        );
        System.out.println("get >>>>>>> "+convertUtcToUserLocalTime(
                photoFilterRequest.getTime()
        ));
        System.out.println("get time server >>>>>>> "+getCurrentTimeInSystemLocalTime());

        return ApiResponse.<List<PhotoResponse>>builder()
                .result(photos)
                .currentPage(photoFilterRequest.getPageCurrent())
                .build();
    }

    @PostMapping("/post")
    public ApiResponse<?> post(@RequestBody @Valid PostRequest postRequest) {

        photoService.post(postRequest);
        System.out.println("post >>>>>>> "+getCurrentTimeInSystemLocalTime());
        return ApiResponse.builder().build();
    }


}
