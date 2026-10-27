package com.workintech.twitter.dto;

import java.time.LocalDateTime;

public record TweetResponse(
        Long id,
        String content,
        String location,
        Long userId,
        String username,
        Long parentTweetId,
        long likeCount,
        long commentCount,
        long retweetCount,
        long viewCount,
        LocalDateTime createdAt) {
}
