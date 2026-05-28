package com.example.Pastach.dto.recommendation;

import java.time.Instant;

public record RecommendationViewReportDTO(
    Instant viewedAt,
    Double viewDuration,  // seconds
    Instant createdAt,
    Long authorId
) {}