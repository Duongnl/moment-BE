package com.moment.moment_BE.dto.request.comment;

import lombok.Data;


@Data
public class CommentFilterRequest {

    private int page = 0;
    private int perPage = 10;
    private String sort = "createdAt"; // tên cột cần sắp xếp
    private String order = "desc";     // asc | desc

    public void setPerPage(int perPage) {
        this.perPage = Math.min(perPage, 100); // giới hạn
    }
}
