package com.example.Pastach.kafka.event.reaction;

import com.example.Pastach.model.Reaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionUpdatedEvent {
    
    private String eventType = "reaction.updated";
    private String eventId;
    private Instant timestamp;
    private ReactionUpdatePayload payload;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReactionUpdatePayload {
        private Long id;
        private String targetType;
        private Long targetId;
        private Long authorId;
        private String type;  // new type (LIKE or DISLIKE)
        private Instant updatedAt;
    }
    
    public static ReactionUpdatedEvent from(Reaction reaction) {
        ReactionUpdatePayload payload = new ReactionUpdatePayload(
            reaction.getId(),
            reaction.getTargetType().name(),
            reaction.getTargetId(),
            reaction.getAuthorId(),
            reaction.getType().name(),
            Instant.now()
        );
        
        return new ReactionUpdatedEvent(
            "reaction.updated",
            UUID.randomUUID().toString(),
            Instant.now(),
            payload
        );
    }
}