package com.example.Pastach.repository;

import com.example.Pastach.model.Reaction;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByTargetTypeAndTargetIdAndAuthorId(
            ReactionTargetType targetType,
            Long targetId,
            Long authorId
    );

    Page<Reaction> findByTargetTypeAndTargetId(
            ReactionTargetType targetType,
            Long targetId,
            Pageable pageable
    );

    // fast counter without JOINs
    long countByTargetTypeAndTargetIdAndType(
            ReactionTargetType targetType,
            Long targetId,
            ReactionType type
    );

    // toggle reaction
    void deleteByTargetTypeAndTargetIdAndAuthorId(
            ReactionTargetType targetType,
            Long targetId,
            Long authorId
    );
}