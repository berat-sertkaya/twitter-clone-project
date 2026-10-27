package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByUser_IdOrderByCreatedAtDesc(Long userId);
    List<Tweet> findByParentTweet_Id(Long parentTweetId);
    long countByParentTweet_Id(Long parentTweetId);
    Optional<Tweet> findByUser_IdAndParentTweet_Id(Long userId, Long parentTweetId);
}
