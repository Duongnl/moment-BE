package com.moment.moment_BE.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRequest {

    /*
    * Class nay de nhan token.
    * Muc dich phuc vu cho viec kiem tra token co hop le hay khong
    * */

    String token;
}
