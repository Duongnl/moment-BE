package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Account;
import com.moment.moment_BE.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<FcmTokenEntity, String> {

    Optional<FcmTokenEntity> findByToken(String token);

    List<FcmTokenEntity> findAllByAccountId(String accountId);

}


