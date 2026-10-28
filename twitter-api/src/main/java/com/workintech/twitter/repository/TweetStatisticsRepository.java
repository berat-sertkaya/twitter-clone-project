package com.workintech.twitter.repository;

import com.workintech.twitter.entity.TweetStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TweetStatisticsRepository extends JpaRepository<TweetStatistics, Long> {

    // Okuma + yazma tek atomik SQL ifadesinde yapilir; es zamanli isteklerde sayac kaybolmaz
    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE statistics SET view_count = view_count + 1 WHERE tweet_id = ?1")
    void incrementViewCount(Long tweetId);
}
