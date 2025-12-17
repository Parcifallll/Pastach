package com.example.Pastach.kafka.event.post;

import com.example.Pastach.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdatedEvent {
    
    private String eventType = "post.updated";
    private String eventId;
    private Instant timestamp;
    private PostUpdatePayload payload;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostUpdatePayload {
        private Long id;
        private String text;  // only text (no photoUrl yet)
        private Instant updatedAt;
    }
    
    public static PostUpdatedEvent from(Post post) {
        PostUpdatePayload payload = new PostUpdatePayload(
            post.getId(),
            post.getText(),
            Instant.now()
        );
        
        return new PostUpdatedEvent(
            "post.updated",
            UUID.randomUUID().toString(),
            Instant.now(),
            payload
        );
    }
}