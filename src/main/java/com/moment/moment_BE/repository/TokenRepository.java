package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<FcmToken, String> {
    void deleteById(Integer id);
    void deleteByToken(String token);

    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findAllByAccountId(String accountId);

}


