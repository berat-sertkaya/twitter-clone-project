package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tweet")
@Getter
@Setter
@NoArgsConstructor
public class Tweet extends EntityBase {

    @NotBlank(message = "Tweet içeriği boş olamaz")
    @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
    @Column(nullable = false, length = 280)
    private String content;

    @NotNull(message = "Tweet bir kullanıcıya ait olmalı")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 100)
    private String location;

    // Retweet ise orijinal tweet'in referansi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_tweet_id")
    private Tweet parentTweet;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TweetLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToOne(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private TweetStatistics statistics;
}
