package com.moment.moment_BE.service;

import com.moment.moment_BE.dto.request.comment.ChangeStatusRequest;
import com.moment.moment_BE.dto.request.comment.CommentCreateRequest;
import com.moment.moment_BE.dto.request.comment.CommentFilterRequest;
import com.moment.moment_BE.dto.response.AccountResponse;
import com.moment.moment_BE.dto.response.comment.CommentResponse;
import com.moment.moment_BE.dto.response.comment.CommentSocketResponse;
import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.Comment;
import com.moment.moment_BE.entity.Photo;
import com.moment.moment_BE.exception.AppException;
import com.moment.moment_BE.exception.CommentErrorCode;
import com.moment.moment_BE.mapper.CommentMapper;
import com.moment.moment_BE.repository.CommentRepository;
import com.moment.moment_BE.repository.PhotoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.moment.moment_BE.exception.CommentErrorCode.COMMENT_NOT_FOUND;
import static com.moment.moment_BE.utils.DateTimeUtils.getCurrentTimeInSystemLocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    PhotoRepository photoRepository;
    AuthenticationService authenticationService;
    CommentMapper commentMapper;
    SimpMessagingTemplate messagingTemplate;

//    private final PhotoRepository photoRepository;


    @Transactional
    public CommentResponse changeStatus(ChangeStatusRequest request) {
        Comment comment = commentRepository
                .findById(request.getCommentId())
                .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        // Cập nhật status
        comment.setStatus(request.getStatus());
        CommentResponse response = commentMapper.toCommentResponse(comment);
        response.setReplyCount(
                (int) comment.getCommentReplies()
                        .stream()
                        .filter(reply -> !"inactive".equals(reply.getStatus()))
                        .count()
        );
        return response;
    }

    public Page<CommentResponse> getCommentPageOfPhoto(
            CommentFilterRequest request,
            Integer photoId,
            LocalDateTime beforeCreatedAt
    ) {
        Pageable pageable = PageRequest.of(0, request.getPerPage(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findCommentsByPhotoWithCursor(photoId, beforeCreatedAt, "inactive", pageable);
        return commentPage.map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(
                    (int) comment.getCommentReplies()
                            .stream()
                            .filter(reply -> !"inactive".equals(reply.getStatus()))
                            .count()
            );
            response.setAuthorAvatar(getUrlAvtAccount(comment.getAccount().getId()));
            return response;
        });
    }

    public String getUrlAvtAccount(String accountId) {
        Photo photoOptional = photoRepository.findByAccount_IdAndStatus(accountId, 2);
        if (photoOptional == null) {
            return null;
        }
        return photoOptional.getUrl();
    }

    //type =1 photo
    public Page<CommentResponse> getCommentReply(CommentFilterRequest request, String commentIdReq) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPerPage(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String commentId = Optional.ofNullable(commentIdReq).orElse("");

        if (!commentId.isEmpty()) {
            Page<Comment> commentPage = commentRepository
                    .findByParentComment_IdAndStatusNot(Integer.parseInt(commentId), "inactive", pageable);
            return commentPage.map(comment -> {
                CommentResponse response = commentMapper.toCommentResponse(comment);
                response.setReplyCount(
                        (int) comment.getCommentReplies()
                                .stream()
                                .filter(reply -> !"inactive".equals(reply.getStatus()))
                                .count()
                );
                return response;
            });
        } else throw new AppException(COMMENT_NOT_FOUND);
    }

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {

        // Kiểm tra photoId
        if (request.getPhotoId() == null) {
            throw new IllegalArgumentException("Photo ID là bắt buộc");
        }

        Optional<Photo> photo = photoRepository.findById(request.getPhotoId());
        if (!photo.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy ảnh với ID: " + request.getPhotoId());
        }

        Optional<Comment> commentParentOptional = Optional.empty();
        Comment commentParent = null;

        if (request.getCommentParentId() != null) {
            commentParentOptional = commentRepository.findById(Integer.parseInt(request.getCommentParentId()));
            if (commentParentOptional.isPresent() && "active".equals(commentParentOptional.get().getStatus())) {
                commentParent = commentParentOptional.get();
            }
        }

        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .parentComment(commentParent)
                .status("active")
                .account(authenticationService.getMyAccount(1))
                .content(request.getContent())
                .photo(photo.get()) // Sử dụng photo.get() vì đã kiểm tra
                .build();

        Comment commentSaved = commentRepository.save(comment);
        CommentSocketResponse response = commentMapper.toCommentSocketResponse(comment);
        response.setReplyCount(0);
        response.setPath(buildCommentPath(commentSaved));
        pushRequestCommentSocket(response, request.getPhotoId());
        return commentMapper.toCommentResponse(comment);
    }

    private List<Integer> buildCommentPath(Comment parent) {
        Integer parentId = parent.getId();
        List<Integer> path = new ArrayList<>();
        while (parent != null) {
            if (!Objects.equals(parentId, parent.getId())) {
                path.add(0, parent.getId()); // add vào đầu để giữ thứ tự từ gốc đến cha
            }
            parent = parent.getParentComment();
        }
        return path;
    }

    public void pushRequestCommentSocket(CommentSocketResponse commentResponse, Integer photoId) {
        messagingTemplate.convertAndSend("/topic/photo/" + photoId + "/comment", commentResponse);
    }


}

