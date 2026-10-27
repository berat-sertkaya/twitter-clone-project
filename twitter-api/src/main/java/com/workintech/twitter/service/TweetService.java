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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final TweetLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    public TweetResponse create(TweetRequest request, String username) {
        User user = userService.getByUsername(username);
        Tweet tweet = newTweet(request.content(), request.location(), user, null);
        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional(readOnly = true)
    public List<TweetResponse> findByUserId(Long userId) {
        userService.getById(userId);
        return tweetRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public TweetResponse findById(Long id) {
        Tweet tweet = getTweetOrThrow(id);
        if (tweet.getStatistics() != null) {
            tweet.getStatistics().setViewCount(tweet.getStatistics().getViewCount() + 1);
        }
        return toResponse(tweet);
    }

    @Transactional
    public TweetResponse update(Long id, TweetRequest request, String username) {
        Tweet tweet = getTweetOrThrow(id);
        checkOwner(tweet, username, "Sadece tweet sahibi tweeti güncelleyebilir");
        if (tweet.getParentTweet() != null) {
            throw new ApiException("Retweet güncellenemez", HttpStatus.BAD_REQUEST);
        }
        tweet.setContent(request.content());
        tweet.setLocation(request.location());
        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional
    public void delete(Long id, String username) {
        Tweet tweet = getTweetOrThrow(id);
        checkOwner(tweet, username, "Sadece tweet sahibi tweeti silebilir");
        // Bu tweet'in retweet'leri FK ile bagli oldugu icin once onlar silinir
        tweetRepository.deleteAll(tweetRepository.findByParentTweet_Id(id));
        tweetRepository.delete(tweet);
    }

    @Transactional
    public TweetResponse retweet(Long tweetId, String username) {
        Tweet target = getTweetOrThrow(tweetId);
        // Retweet'in retweet'i yapilirsa kok tweet'e baglanir
        Tweet original = target.getParentTweet() != null ? target.getParentTweet() : target;
        User user = userService.getByUsername(username);
        if (tweetRepository.findByUser_IdAndParentTweet_Id(user.getId(), original.getId()).isPresent()) {
            throw new ApiException("Bu tweeti zaten retweet ettiniz", HttpStatus.CONFLICT);
        }
        Tweet retweet = newTweet(original.getContent(), original.getLocation(), user, original);
        return toResponse(tweetRepository.save(retweet));
    }

    @Transactional
    public void deleteRetweet(Long id, String username) {
        Tweet retweet = getTweetOrThrow(id);
        if (retweet.getParentTweet() == null) {
            throw new ApiException("Bu tweet bir retweet değil", HttpStatus.BAD_REQUEST);
        }
        checkOwner(retweet, username, "Sadece retweet sahibi retweeti silebilir");
        tweetRepository.delete(retweet);
    }

    public Tweet getTweetOrThrow(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Tweet bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    public TweetResponse toResponse(Tweet t) {
        long views = t.getStatistics() == null ? 0 : t.getStatistics().getViewCount();
        return new TweetResponse(
                t.getId(),
                t.getContent(),
                t.getLocation(),
                t.getUser().getId(),
                t.getUser().getUsername(),
                t.getParentTweet() == null ? null : t.getParentTweet().getId(),
                likeRepository.countByTweet_Id(t.getId()),
                commentRepository.countByTweet_Id(t.getId()),
                tweetRepository.countByParentTweet_Id(t.getId()),
                views,
                t.getCreatedAt());
    }

    private Tweet newTweet(String content, String location, User user, Tweet parent) {
        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setLocation(location);
        tweet.setUser(user);
        tweet.setParentTweet(parent);
        TweetStatistics stats = new TweetStatistics();
        stats.setTweet(tweet);
        tweet.setStatistics(stats);
        return tweet;
    }

    private void checkOwner(Tweet tweet, String username, String message) {
        if (!tweet.getUser().getUsername().equals(username)) {
            throw new ApiException(message, HttpStatus.FORBIDDEN);
        }
    }
}
