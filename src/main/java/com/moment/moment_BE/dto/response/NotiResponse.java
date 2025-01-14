package com.moment.moment_BE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotiResponse {
    int id;
    String name;
    String userName;
    String message;
    LocalDateTime createdAt;
    String urlAvt;
    String urlPhoto;
    String status;
}
