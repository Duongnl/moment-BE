package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Friend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
        List<Friend> findByAccountUser_IdAndStatusInOrderByRequestedAtDesc(
                        String accountId,
                        List<String> statuses,
                        Pageable pageable);

        List<Friend> findByAccountUser_IdAndStatusAndAcceptedAtLessThanEqualOrderByAcceptedAtDesc(String accountId, String status, LocalDateTime acceptedAt, Pageable pageable);

        List<Friend> findByAccountUser_IdAndAccountInitiator_IdAndStatusAndRequestedAtLessThanEqualOrderByRequestedAtDesc(String accountId, String accountInitiatorId,
                        String status,LocalDateTime acceptedAt, Pageable pageable);

        List<Friend> findByAccountUser_IdAndAccountInitiator_IdNotAndStatusAndRequestedAtLessThanEqualOrderByRequestedAtDesc(String accountId, String accountInitiatorId,
                        String status,LocalDateTime acceptedAt, Pageable pageable);

        Optional<Friend> findByAccountUser_IdAndAccountFriend_IdAndStatus(String AccountUser_id,
                        String AccountFriend_Id, String status);

        List<Friend> findByAccountUser_IdAndAccountInitiator_IdNotAndStatusAndRequestedAtBetweenOrderByRequestedAtDesc(
                String accountId, String initiatorId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

        Optional<Friend> findByAccountUser_IdAndAccountFriend_Id(String AccountUser_id, String AccountFriend_Id);

        List<Friend> findByAccountUser_IdAndAccountFriend_IdInAndStatusNot(String accountUserId,List<String> accountFriendIds,String status);

        @Query("SELECT COUNT(f) FROM  Friend f " +
                "WHERE f.accountUser.id IN :accountUserId " +
                "AND f.accountInitiator.id IN :accountUserId " +
                "AND f.status IN :status "
        )
        int countFriendSent (@Param("accountUserId") String accountUserId,
                         @Param("status") String status);
        @Query("SELECT COUNT(f) FROM  Friend f " +
                "WHERE f.accountUser.id IN :accountUserId " +
                "AND f.accountInitiator.id NOT IN :accountUserId " +
                "AND f.status IN :status "
        )
        int countFriendInvited (@Param("accountUserId") String accountUserId,
                         @Param("status") String status);

        @Query("SELECT COUNT(f) FROM  Friend f " +
                "WHERE f.accountUser.id IN :accountUserId " +
                "AND f.status IN :status "
        )
        int countFriend (@Param("accountUserId") String accountUserId,
                                @Param("status") String status);
}
