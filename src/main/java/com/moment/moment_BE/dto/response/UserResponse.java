package com.moment.moment_BE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

//    Tra ve thong tin ca nhan

    String id;
    String userName;
    String email;
    String phoneNumber;
    LocalDateTime createdAt;
    String name;
    LocalDate birthday;
    String address;
    String urlPhoto;
    int status;
}
