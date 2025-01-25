package com.moment.moment_BE.dto.request;

import com.moment.moment_BE.exception.Regex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileFilterRequest {
    @Pattern(regexp = Regex.USER_NAME,message = "USER_NAME_INVALID")
    @NotNull(message = "USER_NAME_INVALID")
    String userName;
    int pageCurrent;
    @NotBlank(message = "NOT_BLANK")
    String time;
}
