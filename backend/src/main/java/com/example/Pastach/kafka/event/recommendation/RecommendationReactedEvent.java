package com.example.Pastach.kafka.event.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationReactedEvent {

    private String eventType = "recommendation.reacted";
    private String eventId;
    private String timestamp;
    private RecommendationReactedPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendationReactedPayload {
        private Long userId;
        private Long postId;
        private String reaction;
    }

    public static RecommendationReactedEvent from(Long userId, Long postId, String reaction) {
        RecommendationReactedPayload payload = new RecommendationReactedPayload(
                userId,
                postId,
                reaction
        );

        return new RecommendationReactedEvent(
                "recommendation.reacted",
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                payload
        );
    }
}