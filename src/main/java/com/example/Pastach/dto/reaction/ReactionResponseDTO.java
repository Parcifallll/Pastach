package com.example.Pastach.dto.reaction;

import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.ReactionType;

import java.time.Instant;

public record ReactionResponseDTO(Long id,
                                  Long authorId,
                                  ReactionType type,
                                  Instant createdAt) {
}
