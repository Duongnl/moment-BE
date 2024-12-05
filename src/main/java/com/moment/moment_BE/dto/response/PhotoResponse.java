package com.moment.moment_BE.dto.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhotoResponse {
    private Integer id;
    private String urlPhoto;
    private String caption;
    private LocalDateTime createdAt;

    private String name;
    private String urlAvt;
    private String userName;

}
