package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Profile;
import org.springframework.data.domain.Example;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository  extends JpaRepository<Profile,Integer> {
    Profile findByAccount_UserName (String username);

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
