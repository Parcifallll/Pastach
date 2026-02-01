package com.example.Pastach.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// Reaction might be put on POST, COMMENT - polymorphic
@Entity
@Table(name = "reactions", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Reaction {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReactionTargetType targetType;  // POST, COMMENT

    @Column(name = "target_id", nullable = false)
    private Long targetId;  // postId, commentId

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReactionType type;  // LIKE, DISLIKE

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Reaction(ReactionTargetType targetType, Long targetId, Long authorId, ReactionType type) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.authorId = authorId;
        this.type = type;
    }
}