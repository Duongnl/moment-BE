package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
    boolean existsByUserName(String username);
    Optional<Account> findByUserNameAndStatus (String userName, int status);
    Optional <Account> findByIdAndStatus (String accountId, int status);
}
