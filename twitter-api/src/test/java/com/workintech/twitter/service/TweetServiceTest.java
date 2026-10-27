package com.workintech.twitter.service;

import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetStatistics;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetLikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

    @Mock private TweetRepository tweetRepository;
    @Mock private TweetLikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserService userService;
    @InjectMocks private TweetService tweetService;

    private User user(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private Tweet tweet(Long id, User owner) {
        Tweet t = new Tweet();
        t.setId(id);
        t.setContent("merhaba");
        t.setUser(owner);
        TweetStatistics s = new TweetStatistics();
        s.setTweet(t);
        t.setStatistics(s);
        return t;
    }

    @Test
    void create_savesTweetWithOwner() {
        when(userService.getByUsername("berat")).thenReturn(user(1L, "berat"));
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArgument(0));

        TweetResponse response = tweetService.create(new TweetRequest("merhaba dünya", "Ankara"), "berat");

        assertEquals("merhaba dünya", response.content());
        assertEquals("berat", response.username());
        assertNull(response.parentTweetId());
    }

    @Test
    void findById_incrementsViewCount() {
        Tweet t = tweet(1L, user(1L, "berat"));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(t));

        TweetResponse response = tweetService.findById(1L);

        assertEquals(1, response.viewCount());
    }

    @Test
    void findById_notFound_throws404() {
        when(tweetRepository.findById(99L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> tweetService.findById(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void delete_byNonOwner_throws403() {
        Tweet t = tweet(5L, user(2L, "baska"));
        when(tweetRepository.findById(5L)).thenReturn(Optional.of(t));

        ApiException ex = assertThrows(ApiException.class, () -> tweetService.delete(5L, "berat"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(tweetRepository, never()).delete(any(Tweet.class));
    }

    @Test
    void delete_byOwner_deletesTweet() {
        Tweet t = tweet(5L, user(1L, "berat"));
        when(tweetRepository.findById(5L)).thenReturn(Optional.of(t));

        tweetService.delete(5L, "berat");

        verify(tweetRepository).delete(t);
    }

    @Test
    void update_byNonOwner_throws403() {
        Tweet t = tweet(5L, user(2L, "baska"));
        when(tweetRepository.findById(5L)).thenReturn(Optional.of(t));

        assertThrows(ApiException.class,
                () -> tweetService.update(5L, new TweetRequest("yeni", null), "berat"));
    }

    @Test
    void retweet_duplicate_throws409() {
        Tweet original = tweet(1L, user(1L, "berat"));
        User me = user(2L, "ahmet");
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(original));
        when(userService.getByUsername("ahmet")).thenReturn(me);
        when(tweetRepository.findByUser_IdAndParentTweet_Id(2L, 1L)).thenReturn(Optional.of(new Tweet()));

        ApiException ex = assertThrows(ApiException.class, () -> tweetService.retweet(1L, "ahmet"));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void retweet_createsTweetLinkedToOriginal() {
        Tweet original = tweet(1L, user(1L, "berat"));
        User me = user(2L, "ahmet");
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(original));
        when(userService.getByUsername("ahmet")).thenReturn(me);
        when(tweetRepository.findByUser_IdAndParentTweet_Id(2L, 1L)).thenReturn(Optional.empty());
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArgument(0));

        TweetResponse response = tweetService.retweet(1L, "ahmet");

        assertEquals(1L, response.parentTweetId());
        assertEquals("ahmet", response.username());
    }

    @Test
    void deleteRetweet_onNormalTweet_throws400() {
        Tweet t = tweet(5L, user(1L, "berat"));
        when(tweetRepository.findById(5L)).thenReturn(Optional.of(t));

        ApiException ex = assertThrows(ApiException.class, () -> tweetService.deleteRetweet(5L, "berat"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
