package com.workintech.twitter.service;

import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetLike;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.TweetLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final TweetLikeRepository likeRepository;
    private final TweetService tweetService;
    private final UserService userService;

    @Transactional
    public void like(Long tweetId, String username) {
        Tweet tweet = tweetService.getTweetOrThrow(tweetId);
        User user = userService.getByUsername(username);
        if (likeRepository.findByTweet_IdAndUser_Id(tweetId, user.getId()).isPresent()) {
            throw new ApiException("Bu tweeti zaten beğendiniz", HttpStatus.CONFLICT);
        }
        TweetLike like = new TweetLike();
        like.setTweet(tweet);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void dislike(Long tweetId, String username) {
        User user = userService.getByUsername(username);
        TweetLike like = likeRepository.findByTweet_IdAndUser_Id(tweetId, user.getId())
                .orElseThrow(() -> new ApiException("Bu tweeti beğenmemişsiniz", HttpStatus.NOT_FOUND));
        likeRepository.delete(like);
    }
}
