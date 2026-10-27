package com.workintech.twitter.controller;

import com.workintech.twitter.dto.TweetActionRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
@RequiredArgsConstructor
public class RetweetController {

    private final TweetService tweetService;

    @PostMapping({"", "/"})
    public ResponseEntity<TweetResponse> retweet(@Valid @RequestBody TweetActionRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.retweet(request.tweetId(), auth.getName()));
    }

    // {id} = retweet'in kendi id'si (retweet POST cevabinda donen id)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetweet(@PathVariable Long id, Authentication auth) {
        tweetService.deleteRetweet(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
