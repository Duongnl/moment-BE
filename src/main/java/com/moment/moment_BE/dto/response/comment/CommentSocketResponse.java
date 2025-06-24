package com.moment.moment_BE.dto.response.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentSocketResponse {
    String id;
    String authorId;
    String authorName;
    String authorAvatar;
    String content;
    Integer replyCount;
    String createdAt;
    List<Integer> path;
}
