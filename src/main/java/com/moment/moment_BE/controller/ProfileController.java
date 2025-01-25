package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.ProfileFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.ProfileResponse;
import com.moment.moment_BE.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;
    @PostMapping()
    public ApiResponse<?> getAll(@RequestBody @Valid ProfileFilterRequest profileFilter) {
             return ApiResponse.builder().result(profileService.getProfileByUserName(profileFilter)).build();
    }

//    @GetMapping("/getStatus")
//    public ApiResponse<?> getStatus ()
}
