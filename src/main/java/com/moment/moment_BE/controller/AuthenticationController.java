package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.AuthenticationRequest;
import com.moment.moment_BE.dto.request.IntrospectRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.AuthenticationResponse;
import com.moment.moment_BE.dto.response.IntrospectResponse;
import com.moment.moment_BE.dto.response.UserResponse;
import com.moment.moment_BE.service.AccountService;
import com.moment.moment_BE.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;


    //    dang nhap, neu dung thi tao ra token
    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate (@RequestBody AuthenticationRequest authenticationRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(authenticationRequest))
                .build();

    }

    //    kiem tra token xem co hop le khong
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result =  authenticationService.introspect(request); // tra ve tru thi dung, false thi sai
//        truyen result ben tren vao aauthenticationrespone r truyen  authenticationresponse vao result cua ApiResponse
//        kieu khoi tao apirespone voi  result la result ben tren
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo () {
        return ApiResponse.<UserResponse>builder()
                .result(authenticationService.getMyInfo())

                .build();
    }


    @GetMapping("/google-login")
    public ApiResponse<AuthenticationResponse> authenticateLoginGoogle(@RequestParam String code) {


        if (code == null || code.isEmpty()) {
            return ApiResponse.<AuthenticationResponse>builder()
                    .status(401)
                    .message("Invalid Google token")
                    .build();
        } else {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            String token=authenticationService.getToken(code);
            System.out.println(token);
            authenticationResponse.setToken(token);
            authenticationResponse.setAuthenticated(true);
            return ApiResponse.<AuthenticationResponse>builder()
                    .result(authenticationResponse)
                    .build();
        }


    }


}
