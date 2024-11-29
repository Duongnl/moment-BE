package com.moment.moment_BE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {
    /*
    * Class nay de tra ve dung sai khi kiem tra token
    * Muc dich cho nguoi dung biet token co dung hay khong
    * */

    boolean valid;

}
