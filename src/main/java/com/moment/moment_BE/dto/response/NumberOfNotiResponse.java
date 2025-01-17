package com.moment.moment_BE.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// An nhung bien co  gi tri la null di
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NumberOfNotiResponse {
    int numberOfNotiAll;
    int numberOfNotiUnread;
    int numberOfNotiNew;
}
