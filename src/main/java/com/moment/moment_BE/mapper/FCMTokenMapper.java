package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.FCMTokenResponse;
import com.moment.moment_BE.dto.response.comment.CommentResponse;
import com.moment.moment_BE.dto.response.comment.CommentSocketResponse;
import com.moment.moment_BE.entity.Comment;
import com.moment.moment_BE.entity.FcmToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FCMTokenMapper {
    public FCMTokenResponse toFCMTokenResponse(FcmToken token);
}
