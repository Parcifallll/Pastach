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
public class PostCreatedEvent {

    private String eventType = "post.created";
    private String eventId;
    private String timestamp;
    private PostPayload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostPayload {
        private Long id;
        private Long authorId;
        private String text;
        private String photoUrl;
        private Instant createdAt;
    }

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
                Instant.now().toString(),
                payload
        );
    }
}