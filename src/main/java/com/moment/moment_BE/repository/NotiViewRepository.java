package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.NotiView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiViewRepository extends JpaRepository<NotiView,Integer> {
    boolean existsByAccount_IdAndNoti_Id(String accountId,int photoId);

}
