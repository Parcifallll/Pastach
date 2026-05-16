package com.example.Pastach.service;

import com.example.Pastach.dto.recommendation.RecommendationScoresDTO;
import com.example.Pastach.dto.recommendation.RecommendedPostDTO;
import com.example.Pastach.kafka.KafkaProducerService;
import com.example.Pastach.model.Post;
import com.example.Pastach.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationAnalyticsService {

    private final PostRepository postRepository;
    private final KafkaProducerService kafkaProducer;

    @Transactional(readOnly = true)
    public void logRecommendationsReceived(Long userId, List<RecommendationScoresDTO> scores) {
        if (scores.isEmpty()) {
            return;
        }

        // fetch post data in one query
        List<Long> postIds = scores.stream()
                .map(RecommendationScoresDTO::postId)
                .collect(Collectors.toList());

        List<Post> posts = postRepository.findAllById(postIds);
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        // build analytics events and send to Kafka
        scores.forEach(score -> {
            Post post = postMap.get(score.postId());
            if (post != null) {
                RecommendedPostDTO event = RecommendedPostDTO.builder()
                        .id(score.postId())
                        .authorId(post.getAuthorId())
                        .createdAt(post.getCreatedAt())
                        .similarityScore(score.similarityScore())
                        .recencyScore(score.recencyScore())
                        .build();

                kafkaProducer.sendRecommendationReceived(userId, event);
                log.debug("Logged recommendation for user={}, post={}", userId, score.postId());
            }
        });
    }
}