package com.workintech.twitter.service;

import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetLike;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.TweetLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private TweetLikeRepository likeRepository;
    @Mock private TweetService tweetService;
    @Mock private UserService userService;
    @InjectMocks private LikeService likeService;

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("berat");
        return u;
    }

    @Test
    void like_savesNewLike() {
        when(tweetService.getTweetOrThrow(10L)).thenReturn(new Tweet());
        when(userService.getByUsername("berat")).thenReturn(user());
        when(likeRepository.findByTweet_IdAndUser_Id(10L, 1L)).thenReturn(Optional.empty());

        likeService.like(10L, "berat");

        verify(likeRepository).save(any(TweetLike.class));
    }

    @Test
    void like_twice_throws409() {
        when(tweetService.getTweetOrThrow(10L)).thenReturn(new Tweet());
        when(userService.getByUsername("berat")).thenReturn(user());
        when(likeRepository.findByTweet_IdAndUser_Id(10L, 1L)).thenReturn(Optional.of(new TweetLike()));

        ApiException ex = assertThrows(ApiException.class, () -> likeService.like(10L, "berat"));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(likeRepository, never()).save(any(TweetLike.class));
    }

    @Test
    void dislike_removesExistingLike() {
        TweetLike like = new TweetLike();
        when(userService.getByUsername("berat")).thenReturn(user());
        when(likeRepository.findByTweet_IdAndUser_Id(10L, 1L)).thenReturn(Optional.of(like));

        likeService.dislike(10L, "berat");

        verify(likeRepository).delete(like);
    }

    @Test
    void dislike_withoutLike_throws404() {
        when(userService.getByUsername("berat")).thenReturn(user());
        when(likeRepository.findByTweet_IdAndUser_Id(10L, 1L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> likeService.dislike(10L, "berat"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
