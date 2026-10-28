package com.workintech.twitter.service;

import com.workintech.twitter.dto.IdCount;
import com.workintech.twitter.dto.PageResponse;
import com.workintech.twitter.dto.TweetRequest;
import com.workintech.twitter.dto.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.TweetStatistics;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.ApiException;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetLikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.TweetStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetService {

    public static final int MAX_PAGE_SIZE = 50;

    private final TweetRepository tweetRepository;
    private final TweetLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final TweetStatisticsRepository statisticsRepository;
    private final UserService userService;

    @Transactional
    public TweetResponse create(TweetRequest request, String username) {
        User user = userService.getByUsername(username);
        Tweet tweet = newTweet(request.content(), request.location(), user, null);
        return toResponse(tweetRepository.save(tweet));
    }

    @Transactional(readOnly = true)
    public PageResponse<TweetResponse> findByUserId(Long userId, int page, int size) {
        userService.getById(userId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        Page<Tweet> result = tweetRepository.findByUser_Id(userId, pageable);
        return new PageResponse<>(
                toResponses(result.getContent()),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast());
    }

    @Transactional
    public TweetResponse findById(Long id) {
        // Once atomik UPDATE, sonra okuma: tweet guncel sayacla yuklenir
        statisticsRepository.incrementViewCount(id);
        return toResponse(getTweetOrThrow(id));
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

    public TweetResponse toResponse(Tweet tweet) {
        return toResponses(List.of(tweet)).get(0);
    }

    /**
     * Tweet listesini response'a cevirir. Like / yorum / retweet sayilari
     * tweet basina ayri sorgu yerine, tum liste icin 3 toplu (GROUP BY) sorguyla alinir.
     */
    public List<TweetResponse> toResponses(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
            return List.of();
        }
        List<Long> ids = tweets.stream().map(Tweet::getId).toList();
        Map<Long, Long> likes = toMap(likeRepository.countByTweetIds(ids));
        Map<Long, Long> comments = toMap(commentRepository.countByTweetIds(ids));
        Map<Long, Long> retweets = toMap(tweetRepository.countRetweetsByTweetIds(ids));

        return tweets.stream()
                .map(t -> new TweetResponse(
                        t.getId(),
                        t.getContent(),
                        t.getLocation(),
                        t.getUser().getId(),
                        t.getUser().getUsername(),
                        t.getParentTweet() == null ? null : t.getParentTweet().getId(),
                        likes.getOrDefault(t.getId(), 0L),
                        comments.getOrDefault(t.getId(), 0L),
                        retweets.getOrDefault(t.getId(), 0L),
                        t.getStatistics() == null ? 0 : t.getStatistics().getViewCount(),
                        t.getCreatedAt()))
                .toList();
    }

    private Map<Long, Long> toMap(List<IdCount> rows) {
        return rows.stream().collect(Collectors.toMap(IdCount::id, IdCount::count));
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
