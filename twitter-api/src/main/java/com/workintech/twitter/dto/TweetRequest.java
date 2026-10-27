package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TweetRequest(
        @NotBlank(message = "Tweet içeriği boş olamaz") @Size(max = 280) String content,
        @Size(max = 100) String location) {
}
