package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Noti;
import com.moment.moment_BE.entity.Photo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotiRepository extends JpaRepository<Noti,Integer> {

    List<Noti> findByAccount_IdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(List<String> accountIds, LocalDateTime startTime, Pageable pageable);
    List<Noti> findByAccount_IdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(List<String> accountIds, LocalDateTime startTime);
    int countByAccount_IdInAndCreatedAtLessThanEqual(List<String> accountIds, LocalDateTime startTime);



}
