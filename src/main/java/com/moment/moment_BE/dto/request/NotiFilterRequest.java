package com.moment.moment_BE.dto.request;

import com.moment.moment_BE.exception.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotiFilterRequest {

    int pageCurrent;

//    @NotBlank(message = "NOT_BLANK")
    @NotNull(message = "NOT_NULL")
    LocalDateTime time;

//    int lastNotiId;

    @Pattern(regexp = Regex.NOTI_STATUS,message = "NOTI_STATUS_INVALID")
    @NotNull(message = "NOTI_STATUS_INVALID")
    String status;

//    int offset;
    int limit;

}
