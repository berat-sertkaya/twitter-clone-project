package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
public class TweetStatistics extends EntityBase {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tweet_id", nullable = false, unique = true)
    private Tweet tweet;

    @PositiveOrZero
    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;
}
