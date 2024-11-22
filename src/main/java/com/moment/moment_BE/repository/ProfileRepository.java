package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository  extends JpaRepository<Profile,Integer> {
}
