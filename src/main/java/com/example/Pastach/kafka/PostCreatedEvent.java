package com.example.Pastach.kafka;

import com.example.Pastach.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent {
    
    private String eventType = "post.created";
    private String eventId;
    private Instant timestamp;
    private PostPayload payload;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostPayload {
        private Long id;
        private String authorId;
        private String text;
        private String photoUrl;
        private Instant createdAt;
    }
    
    // create event from Post
    public static PostCreatedEvent from(Post post) {
        PostPayload payload = new PostPayload(
            post.getId(),
            post.getAuthorId(),
            post.getText(),
            post.getPhotoUrl(),
            post.getCreatedAt()
        );
        
        return new PostCreatedEvent(
            "post.created",
            UUID.randomUUID().toString(),
            Instant.now(),
            payload
        );
    }
}