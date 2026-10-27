package com.workintech.twitter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "followers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}),
        indexes = {
                @Index(name = "indx1", columnList = "follower_id"),
                @Index(name = "indx2", columnList = "following_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class Follower extends EntityBase {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
}
