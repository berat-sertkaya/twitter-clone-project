package com.workintech.twitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotBlank(message = "Yorum boş olamaz") @Size(max = 280) String text) {
}
