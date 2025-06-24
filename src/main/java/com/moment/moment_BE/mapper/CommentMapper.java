package com.moment.moment_BE.mapper;

import com.moment.moment_BE.dto.response.comment.CommentResponse;
import com.moment.moment_BE.dto.response.comment.CommentSocketResponse;
import com.moment.moment_BE.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source="account.id", target ="authorId")
    @Mapping(source="account.profile.name", target ="authorName")
    public CommentResponse toCommentResponse(Comment photo);

    @Mapping(source="account.id", target ="authorId")
    @Mapping(source="account.profile.name", target ="authorName")
    public CommentSocketResponse toCommentSocketResponse(Comment photo);

}
