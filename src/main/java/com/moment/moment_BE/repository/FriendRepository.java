package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend,Integer> {
}
