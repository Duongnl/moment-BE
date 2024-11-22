package com.moment.moment_BE.repository;

import com.moment.moment_BE.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo,Integer> {
}
