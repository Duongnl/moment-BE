package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository  extends JpaRepository<Profile,Integer> {
    Profile findByAccount_UserName (String username);
}
