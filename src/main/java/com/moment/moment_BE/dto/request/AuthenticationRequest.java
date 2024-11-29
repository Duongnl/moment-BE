package com.moment.moment_BE.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    /*
    * Class nay nhan mk, tk cua nguoi dung request vao
    * Muc dich la de phuc vu cho viec xu ly login cua he thong
    * */

    String userName;

    String password;

}
