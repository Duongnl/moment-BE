package com.moment.moment_BE.dto.response.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String authorId;
    String userName;
    String authorName;
    String authorAvatar;
    String content;
    Integer replyCount;
    String createdAt;
}
