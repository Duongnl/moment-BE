package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Tìm theo cả 3 trường, status không phải '...'
    // dung tim kiem dang  LIKE '%keyword% cua JPQL
    Page<Comment> findByPhoto_IdAndParentCommentIsNullAndStatusNot(Integer photo_id, String status, Pageable pageable);

    Page<Comment> findByParentComment_IdAndStatusNot(Integer parentComment_id, String status, Pageable pageable);


    // Truy xuất comment theo photoId, nhỏ hơn commentId, giảm dần theo thời gian
    @Query("""
              SELECT c FROM Comment c
              WHERE c.photo.id = :photoId
                AND c.status <> :excludedStatus
                AND (
                  :beforeCreatedAt IS NULL OR 
                  (c.createdAt < :beforeCreatedAt)
                )
              ORDER BY c.createdAt DESC, c.id DESC
            """)
    Page<Comment> findCommentsByPhotoWithCursor(
            @Param("photoId") Integer photoId,
            @Param("beforeCreatedAt") LocalDateTime beforeCreatedAt,
            @Param("excludedStatus") String excludedStatus,
            Pageable pageable
    );


}
