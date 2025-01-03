package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Noti;
import com.moment.moment_BE.entity.Photo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotiRepository extends JpaRepository<Noti,Integer> {

//    -------------------------------------------------------------------------------------------

    @Query("SELECT n FROM  Noti n " +
            "WHERE n.account.id IN :accountIds " +
            "AND n.createdAt < :startTime " +
            "AND n.createdAt > (SELECT f.acceptedAt FROM Friend f WHERE f.accountUser.id = :myAccountId AND f.accountFriend.id = n.account.id)  " +
            "AND NOT EXISTS (SELECT 1 FROM NotiView nv WHERE nv.noti.id = n.id AND nv.account.id = :myAccountId) " +
            "ORDER BY n.createdAt DESC"
    )
    List<Noti> findNotiUnread (@Param("accountIds") List<String> accountIds,  @Param("myAccountId") String myAccountId,@Param("startTime") LocalDateTime startTime , Pageable pageable );

    @Query("SELECT COUNT(n) FROM  Noti n " +
            "WHERE n.account.id IN :accountIds " +
            "AND n.createdAt < :startTime " +
            "AND n.createdAt > (SELECT f.acceptedAt FROM Friend f WHERE f.accountUser.id = :myAccountId AND f.accountFriend.id = n.account.id)  " +
            "AND NOT EXISTS (SELECT 1 FROM NotiView nv WHERE nv.noti.id = n.id AND nv.account.id = :myAccountId) "
    )
    int countNotiUnread (@Param("accountIds") List<String> accountIds,  @Param("myAccountId") String myAccountId,@Param("startTime") LocalDateTime startTime);





    @Query("SELECT n, CASE WHEN EXISTS (SELECT 1 FROM NotiView  nv WHERE nv.noti.id = n.id AND nv.account.id = :myAccountId) " +
            "THEN 'read' ELSE 'unread' END AS status " +
            "FROM Noti n " +
            "WHERE n.account.id IN :accountIds " +
            "AND n.createdAt < :startTime " +
            "AND n.createdAt > (SELECT f.acceptedAt FROM Friend f WHERE f.accountUser.id = :myAccountId AND f.accountFriend.id = n.account.id)  " +
            "ORDER BY n.createdAt DESC"
    )
    List<Object[]> findNotiAll (@Param("accountIds") List<String> accountIds,  @Param("myAccountId") String myAccountId,@Param("startTime") LocalDateTime startTime , Pageable pageable );

    @Query("SELECT COUNT(n) FROM Noti n " +
            "WHERE n.account.id IN :accountIds " +
            "AND n.createdAt < :startTime " +
            "AND n.createdAt > (SELECT f.acceptedAt FROM Friend f WHERE f.accountUser.id = :myAccountId AND f.accountFriend.id = n.account.id)  "
    )
    int countNotiAll (@Param("accountIds") List<String> accountIds,  @Param("myAccountId") String myAccountId,@Param("startTime") LocalDateTime startTime);


}
