package com.example.Pastach.kafka.event.recommendation;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationViewedEvent {

    private String eventType = "recommendation.viewed";
    private String eventId;
    private String timestamp;
    private RecommendationViewedPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendationViewedPayload {
        private Long userId;
        private Long postId;
        private Long authorId;
        private String viewedAt;
        private String createdAt;
        private Double viewDuration;
    }

    public static RecommendationViewedEvent from(Long userId, Long postId, Long authorId, Instant viewedAt, Instant createdAt, Double viewDuration) {
        RecommendationViewedPayload payload = new RecommendationViewedPayload(
                userId,
                postId,
                authorId,
                viewedAt.toString(),
                createdAt.toString(),
                viewDuration
        );

        return new RecommendationViewedEvent(
                "recommendation.viewed",
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                payload
        );
    }
}