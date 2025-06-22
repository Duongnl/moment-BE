package com.moment.moment_BE.controller;

import com.moment.moment_BE.dto.request.comment.ChangeStatusRequest;
import com.moment.moment_BE.dto.request.comment.CommentCreateRequest;
import com.moment.moment_BE.dto.request.comment.CommentFilterRequest;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.comment.CommentResponse;
import com.moment.moment_BE.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("/photo")
    public ApiResponse<List<CommentResponse>> getCommentsByPhoto(
            @RequestParam(required = false) Integer photoId,
            @RequestParam(required = false) LocalDateTime createdAt,
            @ModelAttribute CommentFilterRequest commentFilterRequest
    ) {
        Page<CommentResponse> commentPage = commentService.getCommentPageOfPhoto(commentFilterRequest, photoId, createdAt);
        return ApiResponse.<List<CommentResponse>>builder()
                .result((commentPage.getContent()))
                .totalItems((int)commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }

    @GetMapping("/photo/reply")
    public ApiResponse<List<CommentResponse>> getCommentCPhotoReply(
            @RequestParam(required = true) String commentId,
            @ModelAttribute CommentFilterRequest commentFilterRequest
    ) {
        Page<CommentResponse> commentPage = commentService.getCommentReply(commentFilterRequest, commentId);
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentPage.getContent())
                .totalItems((int)commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }



    @PutMapping("/change-status-admin")
    public ApiResponse<CommentResponse> changeStatus(@RequestBody ChangeStatusRequest changeStatusRequest) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.changeStatus(changeStatusRequest))
                .build();
    }

    @PostMapping("")
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentCreateRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }


}
