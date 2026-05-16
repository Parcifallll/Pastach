package com.example.Pastach.dto.recommendation;

import lombok.Builder;
import java.time.Instant;

@Builder
public record RecommendedPostDTO(
    Long id,                           // post_id
    Long authorId,                     // from posts table
    Instant createdAt,                 // from posts table
    Double similarityScore,            // from rec-sys
    Double recencyScore            // from rec-sys
) {}