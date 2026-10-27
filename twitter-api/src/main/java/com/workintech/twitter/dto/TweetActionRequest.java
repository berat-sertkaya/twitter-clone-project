package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotNull;

// like, dislike ve retweet istekleri icin ortak govde: { "tweetId": 1 }
public record TweetActionRequest(@NotNull(message = "tweetId zorunlu") Long tweetId) {
}
