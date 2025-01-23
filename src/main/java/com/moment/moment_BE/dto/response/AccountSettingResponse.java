package com.moment.moment_BE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountSettingResponse {
    private String name;
    private String email;
    private String userName;
    private LocalDate birthday;
    private String sex;
    private String phoneNumber;
    private String address;
}
