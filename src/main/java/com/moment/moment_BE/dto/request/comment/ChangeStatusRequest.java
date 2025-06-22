package com.moment.moment_BE.dto.request.comment;

import lombok.Data;

@Data
public class ChangeStatusRequest {
    Integer commentId;
    String status;
}
