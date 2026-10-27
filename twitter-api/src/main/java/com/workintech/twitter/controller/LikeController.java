package com.workintech.twitter.controller;

import com.workintech.twitter.dto.TweetActionRequest;
import com.workintech.twitter.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping({"/like", "/like/"})
    public ResponseEntity<Void> like(@Valid @RequestBody TweetActionRequest request, Authentication auth) {
        likeService.like(request.tweetId(), auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping({"/dislike", "/dislike/"})
    public ResponseEntity<Void> dislike(@Valid @RequestBody TweetActionRequest request, Authentication auth) {
        likeService.dislike(request.tweetId(), auth.getName());
        return ResponseEntity.noContent().build();
    }
}
