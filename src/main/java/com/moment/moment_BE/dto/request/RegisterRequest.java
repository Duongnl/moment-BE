package com.moment.moment_BE.dto.request;

import com.moment.moment_BE.exception.Regex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @Pattern(regexp = Regex.NAME,message = "NAME_INVALID")
    @NotNull(message = "NAME_INVALID")
    String name;

    @NotNull(message = "DATE_INVALID")
    LocalDate birthday;

    @Pattern(regexp = Regex.SEX,message = "SEX_INVALID")
    @NotNull(message = "SEX_INVALID")
    String sex;

    @Pattern(regexp = Regex.USER_NAME,message = "USER_NAME_INVALID")
    @NotNull(message = "USER_NAME_INVALID")
    String userName;

    @Pattern(regexp = Regex.PASSWORD,message = "PASSWORD_INVALID")
    @NotNull(message = "PASSWORD_INVALID")
    String password;
}
