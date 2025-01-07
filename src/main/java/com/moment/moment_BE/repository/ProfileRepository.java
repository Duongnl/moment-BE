package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository  extends JpaRepository<Profile,Integer> {

    @Query("SELECT p FROM Profile p JOIN FETCH p.account a WHERE a.userName = :userName")
    Optional<Profile> findByUserName(@Param("userName") String userName);

}
