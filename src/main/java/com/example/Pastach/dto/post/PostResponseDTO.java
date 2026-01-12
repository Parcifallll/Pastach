package com.example.Pastach.dto.post;

import com.example.Pastach.model.Comment;
import com.example.Pastach.model.Reaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostResponseDTO(
        Long id,
        Long authorId,
        String text,
        String photoUrl,
        Instant createdAt,
        long commentsCount,
        long likesCount,
        long dislikesCount) {
}
