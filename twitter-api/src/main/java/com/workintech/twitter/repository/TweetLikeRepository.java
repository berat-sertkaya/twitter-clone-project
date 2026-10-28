package com.workintech.twitter.repository;

import com.workintech.twitter.dto.IdCount;
import com.workintech.twitter.entity.TweetLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TweetLikeRepository extends JpaRepository<TweetLike, Long> {

    Optional<TweetLike> findByTweet_IdAndUser_Id(Long tweetId, Long userId);

    // Verilen tweet'lerin like sayilari: tek sorgu
    @Query("select new com.workintech.twitter.dto.IdCount(l.tweet.id, count(l)) " +
            "from TweetLike l where l.tweet.id in :ids group by l.tweet.id")
    List<IdCount> countByTweetIds(@Param("ids") Collection<Long> ids);
}
