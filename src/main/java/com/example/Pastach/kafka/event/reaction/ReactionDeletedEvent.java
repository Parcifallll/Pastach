package com.example.Pastach.kafka.event.reaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionDeletedEvent {
    
    private String eventType = "reaction.deleted";
    private String eventId;
    private Instant timestamp;
    private ReactionDeletePayload payload;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReactionDeletePayload {
        private Long id;
        private String authorId;  // for invalidating user preferences
    }
    
    public static ReactionDeletedEvent from(Long reactionId, String authorId) {
        ReactionDeletePayload payload = new ReactionDeletePayload(reactionId, authorId);
        
        return new ReactionDeletedEvent(
            "reaction.deleted",
            UUID.randomUUID().toString(),
            Instant.now(),
            payload
        );
    }
}