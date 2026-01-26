package com.example.Pastach.dto.auth;

import java.time.Instant;

public record SessionInfo(
        String sessionId,      // tokenHash for revocation
        Long userId,
        String deviceInfo,
        String ipAddress,
        Instant createdAt
) {
}