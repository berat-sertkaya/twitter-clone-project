package com.workintech.twitter.service;

import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.CommentRepository;
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
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TweetService tweetService;
    @Mock private UserService userService;
    @InjectMocks private CommentService commentService;

    private User user(String username) {
        User u = new User();
        u.setId(1L);
        u.setUsername(username);
        return u;
    }

    // yorum sahibi: ahmet, tweet sahibi: berat
    private Comment comment() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(user("berat"));
        Comment c = new Comment();
        c.setId(7L);
        c.setTweet(tweet);
        c.setUser(user("ahmet"));
        c.setText("güzel tweet");
        return c;
    }

    @Test
    void delete_byCommentOwner_succeeds() {
        Comment c = comment();
        when(commentRepository.findById(7L)).thenReturn(Optional.of(c));

        commentService.delete(7L, "ahmet");

        verify(commentRepository).delete(c);
    }

    @Test
    void delete_byTweetOwner_succeeds() {
        Comment c = comment();
        when(commentRepository.findById(7L)).thenReturn(Optional.of(c));

        commentService.delete(7L, "berat");

        verify(commentRepository).delete(c);
    }

    @Test
    void delete_byStranger_throws403() {
        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment()));

        ApiException ex = assertThrows(ApiException.class, () -> commentService.delete(7L, "yabanci"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void update_byTweetOwner_throws403() {
        when(commentRepository.findById(7L)).thenReturn(Optional.of(comment()));

        assertThrows(ApiException.class,
                () -> commentService.update(7L, new CommentUpdateRequest("değişti"), "berat"));
    }

    @Test
    void update_byCommentOwner_updatesText() {
        Comment c = comment();
        when(commentRepository.findById(7L)).thenReturn(Optional.of(c));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        var response = commentService.update(7L, new CommentUpdateRequest("değişti"), "ahmet");

        assertEquals("değişti", response.text());
    }
}
