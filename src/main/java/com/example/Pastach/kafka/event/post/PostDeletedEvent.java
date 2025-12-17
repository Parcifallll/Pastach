package com.example.Pastach.kafka.event.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDeletedEvent {
    
    private String eventType = "post.deleted";
    private String eventId;
    private Instant timestamp;
    private PostDeletePayload payload;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDeletePayload {
        private Long id;
    }
    
    public static PostDeletedEvent from(Long postId) {
        PostDeletePayload payload = new PostDeletePayload(postId);
        
        return new PostDeletedEvent(
            "post.deleted",
            UUID.randomUUID().toString(),
            Instant.now(),
            payload
        );
    }
}