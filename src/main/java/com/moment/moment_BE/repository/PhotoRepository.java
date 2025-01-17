package com.moment.moment_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moment.moment_BE.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo,Integer> {
    List<Photo> findByAccount_IdInAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(List<String> accountIds, int status, LocalDateTime startTime, Pageable pageable);
    List<Photo> findByAccount_IdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(String accountId , int status ,  LocalDateTime startTime, Pageable pageable  );

    Photo findByAccount_IdAndStatus(String accountId, int status);

    Photo findBySlug(String slug);
}
