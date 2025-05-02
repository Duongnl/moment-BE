package com.moment.moment_BE.dto.request;

import com.moment.moment_BE.exception.Regex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInfoRequest {

    @Pattern(regexp = Regex.NAME,message = "NAME_INVALID")
    @NotNull(message = "NAME_INVALID")
    private String name;

    private String email;

    @Pattern(regexp = Regex.USER_NAME,message = "USER_NAME_INVALID")
    @NotNull(message = "USER_NAME_INVALID")
    private String userName;

    @NotNull(message = "DATE_INVALID")
    private LocalDate birthday;

    private String sex;
    private String phoneNumber;
    private String address;
}
