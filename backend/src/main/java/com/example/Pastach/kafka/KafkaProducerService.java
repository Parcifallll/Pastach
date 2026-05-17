package com.example.Pastach.kafka;

import com.example.Pastach.dto.recommendation.RecommendedPostDTO;
import com.example.Pastach.kafka.event.post.PostCreatedEvent;
import com.example.Pastach.kafka.event.post.PostDeletedEvent;
import com.example.Pastach.kafka.event.post.PostUpdatedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionCreatedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionDeletedEvent;
import com.example.Pastach.kafka.event.reaction.ReactionUpdatedEvent;
import com.example.Pastach.kafka.event.recommendation.RecommendationReactedEvent;
import com.example.Pastach.kafka.event.recommendation.RecommendationReceivedEvent;
import com.example.Pastach.kafka.event.recommendation.RecommendationSentimentUpdatedEvent;
import com.example.Pastach.kafka.event.recommendation.RecommendationViewedEvent;
import com.example.Pastach.model.Post;
import com.example.Pastach.model.Reaction;
import com.example.Pastach.model.ReactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        log.info("KafkaProducerService initialized with KafkaTemplate: {}", kafkaTemplate != null ? "SUCCESS" : "NULL");
    }

    public void sendPostCreated(Post post) {
        try {
            PostCreatedEvent event = PostCreatedEvent.from(post);
            // key: postId for order
            String key = post.getId().toString();

            kafkaTemplate.send("pastach.posts", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent post.created event for post {} to topic {} partition {}",
                                    post.getId(),
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send post.created event for post {}: {}",
                                    post.getId(), ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create post.created event: {}", e.getMessage(), e);
        }
    }

    public void sendPostUpdated(Post post) {
        try {
            PostUpdatedEvent event = PostUpdatedEvent.from(post);
            String key = post.getId().toString();

            kafkaTemplate.send("pastach.posts", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent post.updated event for post {} to topic {} partition {}",
                                    post.getId(),
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send post.updated event for post {}: {}",
                                    post.getId(), ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create post.updated event: {}", e.getMessage(), e);
        }
    }

    public void sendPostDeleted(Long postId) {
        try {
            PostDeletedEvent event = PostDeletedEvent.from(postId);
            String key = postId.toString();

            kafkaTemplate.send("pastach.posts", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent post.deleted event for post {} to topic {} partition {}",
                                    postId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send post.deleted event for post {}: {}",
                                    postId, ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create post.deleted event: {}", e.getMessage(), e);
        }
    }

    // reaction events

    public void sendReactionCreated(Reaction reaction) {
        try {
            ReactionCreatedEvent event = ReactionCreatedEvent.from(reaction);
            //key: authorId for user-based partitions and ordering
            String key = reaction.getAuthorId().toString();

            kafkaTemplate.send("pastach.reactions", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent reaction.created event for reaction {} (author {}) to topic {} partition {}",
                                    reaction.getId(),
                                    reaction.getAuthorId(),
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send reaction.created event for reaction {}: {}",
                                    reaction.getId(), ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create reaction.created event: {}", e.getMessage(), e);
        }
    }

    public void sendReactionUpdated(Reaction reaction) {
        try {
            ReactionUpdatedEvent event = ReactionUpdatedEvent.from(reaction);
            String key = reaction.getAuthorId().toString();

            kafkaTemplate.send("pastach.reactions", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent reaction.updated event for reaction {} (author {}) to topic {} partition {}",
                                    reaction.getId(),
                                    reaction.getAuthorId(),
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send reaction.updated event for reaction {}: {}",
                                    reaction.getId(), ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create reaction.updated event: {}", e.getMessage(), e);
        }
    }

    public void sendReactionDeleted(Long reactionId, Long authorId) {
        try {
            ReactionDeletedEvent event = ReactionDeletedEvent.from(reactionId, authorId);
            String key = authorId.toString();

            kafkaTemplate.send("pastach.reactions", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent reaction.deleted event for reaction {} (author {}) to topic {} partition {}",
                                    reactionId,
                                    authorId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send reaction.deleted event for reaction {}: {}",
                                    reactionId, ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create reaction.deleted event: {}", e.getMessage(), e);
        }
    }

    public void sendRecommendationReceived(Long userId, RecommendedPostDTO recommendation) {
        try {
            RecommendationReceivedEvent event = RecommendationReceivedEvent.from(userId, recommendation);
            // user-based partitions and ordering
            String key = userId.toString();

            kafkaTemplate.send("pastach.recommendations", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent recommendation.received event for user {} post {} to topic {} partition {}",
                                    userId,
                                    recommendation.id(),
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send recommendation.received event for user {} post {}: {}",
                                    userId, recommendation.id(), ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create recommendation.received event: {}", e.getMessage(), e);
        }
    }

    public void sendRecommendationViewed(Long userId, Long postId, Instant viewedAt, Double viewDuration) {
        try {
            RecommendationViewedEvent event = RecommendationViewedEvent.from(userId, postId, viewedAt, viewDuration);
            String key = userId.toString();

            kafkaTemplate.send("pastach.recommendations", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent recommendation.viewed event for user {} post {} to topic {} partition {}",
                                    userId,
                                    postId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send recommendation.viewed event for user {} post {}: {}",
                                    userId, postId, ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create recommendation.viewed event: {}", e.getMessage(), e);
        }
    }

    public void sendRecommendationReacted(Long userId, Long postId, ReactionType reaction) {
        try {
            RecommendationReactedEvent event = RecommendationReactedEvent.from(userId, postId, reaction);
            String key = userId.toString();

            kafkaTemplate.send("pastach.recommendations", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent recommendation.reacted event for user {} post {} to topic {} partition {}",
                                    userId,
                                    postId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send recommendation.reacted event for user {} post {}: {}",
                                    userId, postId, ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create recommendation.reacted event: {}", e.getMessage(), e);
        }
    }

    public void sendRecommendationSentimentUpdated(Long postId, String commentText) {
        try {
            RecommendationSentimentUpdatedEvent event = RecommendationSentimentUpdatedEvent.from(postId, commentText);
            // all sentiment updates for same post go to same partition
            String key = postId.toString();

            kafkaTemplate.send("pastach.recommendations", key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully sent recommendation.sentiment.updated event for post {} to topic {} partition {}",
                                    postId,
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition());
                        } else {
                            log.error("Failed to send recommendation.sentiment.updated event for post {}: {}",
                                    postId, ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to create recommendation.sentiment.updated event: {}", e.getMessage(), e);
        }
    }
}