package com.workintech.twitter.dto;

import java.time.LocalDateTime;

public record CommentResponse(Long id, Long tweetId, Long userId, String username, String text, LocalDateTime createdAt) {
}
