package com.example.Pastach.service;

import com.example.Pastach.grpc.GetRecommendationsRequest;
import com.example.Pastach.grpc.GetRecommendationsResponse;
import com.example.Pastach.grpc.RecommendationServiceGrpc;
import com.example.Pastach.grpc.RecommendedPost;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationGrpcClient {

    private final ManagedChannel channel;
    private final RecommendationServiceGrpc.RecommendationServiceBlockingStub blockingStub;

    public RecommendationGrpcClient(
            @Value("${grpc.host:localhost}") String host,
            @Value("${grpc.port:50051}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = RecommendationServiceGrpc.newBlockingStub(channel);
        log.info("gRPC client is connected to {}:{}", host, port);
    }

    public List<Long> getRecommendedPostIds(Long userId, int limit, boolean excludeAuthorPosts) {
        GetRecommendationsRequest request = GetRecommendationsRequest.newBuilder()
                .setUserId(userId)
                .setLimit(limit)
                .setExcludeAuthorPosts(excludeAuthorPosts)
                .build();

        try {
            GetRecommendationsResponse response = blockingStub.getRecommendations(request);
            log.info("Got {} recommendations for userId={}", response.getTotalCount(), userId);

            return response.getRecommendationsList().stream()
                    .map(RecommendedPost::getPostId)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while getting recommendations: {}", e.getStatus(), e);
            return Collections.emptyList(); // fallback
        }
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
        log.info("gRPC channel closed");
    }
}