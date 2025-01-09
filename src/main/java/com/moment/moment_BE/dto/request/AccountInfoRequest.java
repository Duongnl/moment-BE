package com.moment.moment_BE.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInfoRequest {
    private String name;
    private String email;
    private String userName;
    private LocalDate birthday;
    private String sex;
    private String phoneNumber;
    private String address;
}
