package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Profile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ProfileRepository  extends JpaRepository<Profile,Integer> {

    @Query("SELECT p FROM Profile p JOIN FETCH p.account a WHERE a.userName = :userName")
    Optional<Profile> findByUserName(@Param("userName") String userName);


    @Modifying
    @Transactional
    @Query("UPDATE Profile p SET p.name = :name, p.birthday = :birthday, p.sex = :sex, p.address = :address WHERE p.account.userName = :userName")
    void updateProfileByUserName(@Param("name") String name,
                                 @Param("birthday") LocalDate birthday,
                                 @Param("sex") String sex,
                                 @Param("address") String address,
                                 @Param("userName") String userName);

}
