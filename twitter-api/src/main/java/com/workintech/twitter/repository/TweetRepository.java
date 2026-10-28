package com.workintech.twitter.repository;

import com.workintech.twitter.dto.IdCount;
import com.workintech.twitter.entity.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // user ve statistics tek sorguda gelir (liste icin ekstra sorgu atilmaz)
    @EntityGraph(attributePaths = {"user", "statistics"})
    Page<Tweet> findByUser_Id(Long userId, Pageable pageable);

    List<Tweet> findByParentTweet_Id(Long parentTweetId);

    Optional<Tweet> findByUser_IdAndParentTweet_Id(Long userId, Long parentTweetId);

    // Verilen tweet'lerin retweet sayilari: tek sorgu
    @Query("select new com.workintech.twitter.dto.IdCount(t.parentTweet.id, count(t)) " +
            "from Tweet t where t.parentTweet.id in :ids group by t.parentTweet.id")
    List<IdCount> countRetweetsByTweetIds(@Param("ids") Collection<Long> ids);
}
