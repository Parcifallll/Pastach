package com.example.Pastach.kafka.event.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationSentimentUpdatedEvent {

    private String eventType = "recommendation.sentiment.updated";
    private String eventId;
    private String timestamp;
    private RecommendationSentimentPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendationSentimentPayload {
        private Long postId;
        private Long userId;
        private String commentText;
    }

    public static RecommendationSentimentUpdatedEvent from(Long postId, Long userId, String commentText) {
        RecommendationSentimentPayload payload = new RecommendationSentimentPayload(
                postId,
                userId,
                commentText
        );

        return new RecommendationSentimentUpdatedEvent(
                "recommendation.sentiment.updated",
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                payload
        );
    }
}