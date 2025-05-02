package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByUserName(String username);

    Optional<Account> findByUserNameAndStatus(String userName, int status);

    Optional<Account> findByIdAndStatus(String accountId, int status);

    List<Account> findByUserNameContainingOrPhoneNumberContainingOrEmailContainingOrProfile_NameContainingAndStatus(
            String userName,
            String phoneNumber,
            String email,
            String name,
            int status,
            Pageable Pageable);

    Optional<Account> findByEmailAndStatus(String email, int status);

}


