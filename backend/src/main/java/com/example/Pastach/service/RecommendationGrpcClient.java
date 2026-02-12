package com.example.Pastach.service;

import com.example.Pastach.grpc.GetRecommendationsRequest;
import com.example.Pastach.grpc.GetRecommendationsResponse;
import com.example.Pastach.grpc.RecommendationServiceGrpc;
import com.example.Pastach.grpc.RecommendedPost;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationGrpcClient {

    private final ManagedChannel channel; // tcp + hhtp/2
    private final RecommendationServiceGrpc.RecommendationServiceStub asyncStub;

    public RecommendationGrpcClient(
            @Value("${grpc.host}") String host,
            @Value("${grpc.port}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = RecommendationServiceGrpc.newStub(channel);  // async
        log.info("gRPC client is connected to {}:{}", host, port);
    }

    public CompletableFuture<List<Long>> getRecommendedPostIdsAsync(Long userId, int limit, int offset, boolean excludeAuthorPosts) {  // added offset
        CompletableFuture<GetRecommendationsResponse> responseFuture = new CompletableFuture<>();

        GetRecommendationsRequest request = GetRecommendationsRequest.newBuilder()
                .setUserId(userId)
                .setLimit(limit)
                .setOffset(offset)
                .setExcludeAuthorPosts(excludeAuthorPosts)
                .build();

        asyncStub.withDeadlineAfter(10, TimeUnit.SECONDS) // server response
                .getRecommendations(request, new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(GetRecommendationsResponse response) {
                        responseFuture.complete(response);
                    }

                    @Override
                    public void onError(Throwable t) {
                        responseFuture.completeExceptionally(t);
                    }

                    @Override
                    public void onCompleted() {
                        // single response, not used
                    }
                });

        return responseFuture.thenApply(response -> {
                    log.info("Got {} recommendations for userId={}", response.getTotalCount(), userId);
                    return response.getRecommendationsList().stream()
                            .map(RecommendedPost::getPostId)
                            .collect(Collectors.toList());
                })
                .exceptionally(e -> {
                    log.error("gRPC error while getting recommendations: {}", e.getMessage(), e);
                    return Collections.emptyList();  // Fallback
                });
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
        log.info("gRPC channel closed");
    }
}