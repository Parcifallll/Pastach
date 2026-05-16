package com.example.Pastach.dto.recommendations;

import lombok.Builder;

@Builder
public record RecommendationScoresDTO(
    Long postId,
    Double similarityScore,
    Double recencyScore
) {}