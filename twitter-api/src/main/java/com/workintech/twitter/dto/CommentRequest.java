package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotNull(message = "tweetId zorunlu") Long tweetId,
        @NotBlank(message = "Yorum boş olamaz") @Size(max = 280) String text) {
}
