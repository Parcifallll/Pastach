package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PACKAGE) // for JPA
@Entity
@Table(name = "posts", schema = "public",
        indexes = {
                @Index(name = "idx_posts_author_id", columnList = "author_id"),
                @Index(name = "idx_posts_created_at", columnList = "created_at DESC")
        })
@NoArgsConstructor // for JPA
public class Post {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "photo_url")
    private String photoUrl;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "comments_count", nullable = false, columnDefinition = "bigint default 0")
    private long commentsCount = 0;

    @Column(name = "likes_count", nullable = false, columnDefinition = "bigint default 0")
    private long likesCount = 0;

    @Column(name = "dislikes_count", nullable = false, columnDefinition = "bigint default 0")
    private long dislikesCount = 0;

    // for PostService and PostMapper
    public Post(String text, String photoUrl, Long authorId) {
        this.text = text;
        this.photoUrl = photoUrl;
        this.authorId = authorId;
    }
}