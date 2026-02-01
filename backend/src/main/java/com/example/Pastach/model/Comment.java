package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "text")
    private String text;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "likes_count", nullable = false, columnDefinition = "bigint default 0")
    private long likesCount = 0;

    @Column(name = "dislikes_count", nullable = false, columnDefinition = "bigint default 0")
    private long dislikesCount = 0;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // only when request comments from post
    @JoinColumn(name = "post_id")
    private Post post;

}
