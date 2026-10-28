package com.workintech.twitter.repository;

import com.workintech.twitter.dto.IdCount;
import com.workintech.twitter.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Verilen tweet'lerin yorum sayilari: tek sorgu
    @Query("select new com.workintech.twitter.dto.IdCount(c.tweet.id, count(c)) " +
            "from Comment c where c.tweet.id in :ids group by c.tweet.id")
    List<IdCount> countByTweetIds(@Param("ids") Collection<Long> ids);
}
