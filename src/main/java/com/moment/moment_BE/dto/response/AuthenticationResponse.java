package com.moment.moment_BE.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    /*
    * Class nay tra ve thong tin luc login
    * Muc dich de nguoi dung biet co dang nhap thanh cong hay khong
    * */

    String token;
    boolean authenticated;
}

