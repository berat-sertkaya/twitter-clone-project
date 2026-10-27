package com.workintech.twitter.service;

import com.workintech.twitter.dto.CommentRequest;
import com.workintech.twitter.dto.CommentResponse;
import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TweetService tweetService;
    private final UserService userService;

    @Transactional
    public CommentResponse create(CommentRequest request, String username) {
        Tweet tweet = tweetService.getTweetOrThrow(request.tweetId());
        User user = userService.getByUsername(username);
        Comment comment = new Comment();
        comment.setTweet(tweet);
        comment.setUser(user);
        comment.setText(request.text());
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse update(Long id, CommentUpdateRequest request, String username) {
        Comment comment = getOrThrow(id);
        if (!comment.getUser().getUsername().equals(username)) {
            throw new ApiException("Sadece yorum sahibi yorumu güncelleyebilir", HttpStatus.FORBIDDEN);
        }
        comment.setText(request.text());
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long id, String username) {
        Comment comment = getOrThrow(id);
        boolean isCommentOwner = comment.getUser().getUsername().equals(username);
        boolean isTweetOwner = comment.getTweet().getUser().getUsername().equals(username);
        if (!isCommentOwner && !isTweetOwner) {
            throw new ApiException("Yorumu sadece yorum sahibi veya tweet sahibi silebilir", HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    private Comment getOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    private CommentResponse toResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getTweet().getId(), c.getUser().getId(),
                c.getUser().getUsername(), c.getText(), c.getCreatedAt());
    }
}
