package com.workintech.twitter.repository;

import com.workintech.twitter.entity.TweetLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {
    long countByTweet_Id(Long tweetId);
    Optional<TweetLike> findByTweet_IdAndUser_Id(Long tweetId, Long userId);
}
