package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Friend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Integer> {
    List<Friend> findByAccountUser_IdAndStatusInOrderByRequestedAtDesc(
            String accountId,
            List<String> statuses,
            Pageable pageable
    );
    Optional<Friend> findByAccountUser_IdAndAccountFriend_IdAndStatus(String AccountUser_id, String AccountFriend_Id, String status );

    Optional<Friend> findByAccountUser_IdAndAccountFriend_Id(String AccountUser_id, String AccountFriend_Id );
}
