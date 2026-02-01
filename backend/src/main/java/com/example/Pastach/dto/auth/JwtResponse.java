package com.example.Pastach.dto.auth;

import lombok.Builder;

@Builder
public record JwtResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {
    public JwtResponse(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}