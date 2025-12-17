package com.example.Pastach.kafka;

import com.example.Pastach.kafka.event.post.PostCreatedEvent;
import com.example.Pastach.kafka.event.post.PostDeletedEvent;
import com.example.Pastach.kafka.event.post.PostUpdatedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionCreatedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionDeletedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionUpdatedEvent;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // post events

    public void sendPostCreated(Post post) {
        try {
            PostCreatedEvent event = PostCreatedEvent.from(post);
            kafkaTemplate.send("post.created", post.getId().toString(), event);
            log.info("Sent post.created event for post {}", post.getId());
        } catch (Exception e) {
            log.error("Failed to send post.created: {}", e.getMessage());
        }
    }

    public void sendPostUpdated(Post post) {
        try {
            PostUpdatedEvent event = PostUpdatedEvent.from(post);
            kafkaTemplate.send("post.updated", post.getId().toString(), event);
            log.info("Sent post.updated event for post {}", post.getId());
        } catch (Exception e) {
            log.error("Failed to send post.updated: {}", e.getMessage());
        }
    }

    public void sendPostDeleted(Long postId) {
        try {
            PostDeletedEvent event = PostDeletedEvent.from(postId);
            kafkaTemplate.send("post.deleted", postId.toString(), event);
            log.info("Sent post.deleted event for post {}", postId);
        } catch (Exception e) {
            log.error("Failed to send post.deleted: {}", e.getMessage());
        }
    }

    // reaction posts
    public void sendReactionCreated(Reaction reaction) {
        try {
            ReactionCreatedEvent event = ReactionCreatedEvent.from(reaction);
            kafkaTemplate.send("reaction.created", reaction.getId().toString(), event);
            log.info("Sent reaction.created event for reaction {}", reaction.getId());
        } catch (Exception e) {
            log.error("Failed to send reaction.created: {}", e.getMessage());
        }
    }

    public void sendReactionUpdated(Reaction reaction) {
        try {
            ReactionUpdatedEvent event = ReactionUpdatedEvent.from(reaction);
            kafkaTemplate.send("reaction.updated", reaction.getId().toString(), event);
            log.info("Sent reaction.updated event for reaction {}", reaction.getId());
        } catch (Exception e) {
            log.error("Failed to send reaction.updated: {}", e.getMessage());
        }
    }

    public void sendReactionDeleted(Long reactionId, String authorId) {
        try {
            ReactionDeletedEvent event = ReactionDeletedEvent.from(reactionId, authorId);
            kafkaTemplate.send("reaction.deleted", reactionId.toString(), event);
            log.info("Sent reaction.deleted event for reaction {}", reactionId);
        } catch (Exception e) {
            log.error("Failed to send reaction.deleted: {}", e.getMessage());
        }
    }
}