package com.moment.moment_BE.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendFilterRequest {
    int pageCurrent;

    @NotBlank(message = "NOT_BLANK")
    String time;
}
