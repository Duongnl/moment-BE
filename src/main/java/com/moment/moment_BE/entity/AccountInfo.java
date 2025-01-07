package com.moment.moment_BE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInfo {
    private String name;
    private String email;
    private String userName;
    private LocalDate birthday;
    private String sex;
    private String phoneNumber;
    private String address;
}
