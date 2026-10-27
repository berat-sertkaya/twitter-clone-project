package com.workintech.twitter.controller;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping({"", "/"})
    public ResponseEntity<TweetResponse> create(@Valid @RequestBody TweetRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(request, auth.getName()));
    }

    @GetMapping("/findByUserId")
    public List<TweetResponse> findByUserId(@RequestParam Long userId) {
        return tweetService.findByUserId(userId);
    }

    @GetMapping("/findById")
    public TweetResponse findById(@RequestParam Long id) {
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    public TweetResponse update(@PathVariable Long id, @Valid @RequestBody TweetRequest request, Authentication auth) {
        return tweetService.update(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        tweetService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
