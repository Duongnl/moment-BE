package com.moment.moment_BE.dto.request.comment;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private Integer photoId;
    private String commentParentId;
    private String content;
}
