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
public class ReactionCreatedEvent {

    private String eventType = "reaction.created";
    private String eventId;
    private Instant timestamp;
    private ReactionPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReactionPayload {
        private Long id;
        private String targetType;
        private Long targetId;
        private Long authorId;
        private String type;
        private Instant createdAt;
    }

    // create event from Reaction
    public static ReactionCreatedEvent from(Reaction reaction) {
        ReactionPayload payload = new ReactionPayload(
                reaction.getId(),
                reaction.getTargetType().name(),
                reaction.getTargetId(),
                reaction.getAuthorId(),
                reaction.getType().name(),
                reaction.getCreatedAt()
        );

        return new ReactionCreatedEvent(
                "reaction.created",
                UUID.randomUUID().toString(),
                Instant.now(),
                payload
        );
    }
}