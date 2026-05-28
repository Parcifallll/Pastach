package com.example.Pastach.kafka.event.recommendation;

import com.example.Pastach.dto.recommendation.RecommendedPostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationReceivedEvent {

    private String eventType = "recommendation.received";
    private String eventId;
    private String timestamp;
    private RecommendationPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecommendationPayload {
        private Long userId;
        private Long postId;
        private Long authorId;
        private String createdAt;
        private Double similarityScore;
        private Double recencyScore;
    }

    public static RecommendationReceivedEvent from(Long userId, RecommendedPostDTO dto) {
        RecommendationPayload payload = new RecommendationPayload(
                userId,
                dto.id(),
                dto.authorId(),
                dto.createdAt().toString(),
                dto.similarityScore(),
                dto.recencyScore()
        );

        return new RecommendationReceivedEvent(
                "recommendation.received",
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                payload
        );
    }
}