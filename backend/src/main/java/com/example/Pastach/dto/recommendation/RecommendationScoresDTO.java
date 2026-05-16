package com.example.Pastach.dto.recommendation;

import lombok.Builder;

@Builder
public record RecommendationScoresDTO(
    Long postId,
    Double similarityScore,
    Double recencyScore
) {}