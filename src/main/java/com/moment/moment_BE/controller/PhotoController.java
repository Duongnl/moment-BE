package com.moment.moment_BE.controller;

import java.util.List;

import com.moment.moment_BE.dto.request.PostRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponse.builder()
                .build();
    }

    @PostMapping("/change-avatar")
    public ApiResponse<?> changeAvatar(@RequestBody @Valid PostRequest postRequest) {

        photoService.changeAvatar(postRequest);
        return ApiResponse.builder()
                .build();
    }

    @GetMapping()
    public ApiResponse<PhotoResponse> getPhoto(@RequestParam String post) {
        return ApiResponse.<PhotoResponse>builder()
                .result(photoService.getPhoto(post))
                .build();
    }


}
