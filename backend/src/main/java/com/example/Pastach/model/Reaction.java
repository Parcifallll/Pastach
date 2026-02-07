package com.example.Pastach.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "reactions", schema = "public",
        uniqueConstraints = @UniqueConstraint(name = "reactions_unique_per_target", columnNames = {"target_type", "target_id", "author_id"}),
        indexes = {
                @Index(name = "idx_reactions_target", columnList = "target_type, target_id"),
                @Index(name = "idx_reactions_author", columnList = "author_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class Reaction {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private ReactionTargetType targetType;  // POST, COMMENT

    @Column(name = "target_id", nullable = false)
    private Long targetId;  // postId, commentId

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
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