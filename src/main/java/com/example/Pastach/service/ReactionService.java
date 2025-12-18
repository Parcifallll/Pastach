package com.example.Pastach.service;

import com.example.Pastach.exception.CommentNotFoundException;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.kafka.KafkaProducerService;
import com.example.Pastach.model.*;
import com.example.Pastach.repository.CommentRepository;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final KafkaProducerService kafkaProducer;

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void toggleReaction(ReactionTargetType targetType,
                               Long targetId,
                               ReactionType type,
                               User user) {

        String authorId = user.getId();

        // Validate target exists
        if (targetType == ReactionTargetType.POST) {
            postRepository.findById(targetId)
                    .orElseThrow(() -> new PostNotFoundException(targetId));
        } else {
            commentRepository.findById(targetId)
                    .orElseThrow(() -> new CommentNotFoundException(targetId));
        }

        Optional<Reaction> existing = reactionRepository
                .findByTargetTypeAndTargetIdAndAuthorId(targetType, targetId, authorId);

        Reaction savedReaction = null;
        boolean isNewReaction = false;

        if (existing.isPresent()) {
            Reaction r = existing.get();

            if (r.getType() == type) {
                // Same type - remove reaction
                reactionRepository.delete(r);
                if (targetType == ReactionTargetType.POST) kafkaProducer.sendReactionDeleted(r.getId(), authorId);
            } else {
                // another type - update reaction
                r.setType(type);
                savedReaction = reactionRepository.save(r);
                if (targetType == ReactionTargetType.POST) kafkaProducer.sendReactionUpdated(savedReaction);
            }
        } else {
            // New reaction
            Reaction reaction = new Reaction(targetType, targetId, authorId, type);
            savedReaction = reactionRepository.save(reaction);
            isNewReaction = true;
        }

        updateCounters(targetType, targetId);

        // new reaction
        if (isNewReaction && targetType == ReactionTargetType.POST) {
            kafkaProducer.sendReactionCreated(savedReaction);
        }
    }

    private void updateCounters(ReactionTargetType targetType, Long targetId) {
        if (targetType == ReactionTargetType.POST) {
            Post post = postRepository.findById(targetId).get();
            post.setLikesCount(reactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, ReactionType.LIKE));
            post.setDislikesCount(reactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, ReactionType.DISLIKE));
            postRepository.save(post);
        } else {
            Comment comment = commentRepository.findById(targetId).get();
            comment.setLikesCount(reactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, ReactionType.LIKE));
            comment.setDislikesCount(reactionRepository.countByTargetTypeAndTargetIdAndType(targetType, targetId, ReactionType.DISLIKE));
            commentRepository.save(comment);
        }
    }
}