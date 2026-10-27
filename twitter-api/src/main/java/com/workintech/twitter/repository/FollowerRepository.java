package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
}
