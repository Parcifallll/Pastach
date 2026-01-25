package com.example.Pastach.security;

import com.example.Pastach.dto.auth.JwtResponse;
import com.example.Pastach.dto.auth.LoginDTO;
import com.example.Pastach.dto.auth.SignupDTO;
import com.example.Pastach.model.RefreshToken;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.RefreshTokenRepository;
import com.example.Pastach.repository.UserRepository;
import com.example.Pastach.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse signup(SignupDTO dto, String deviceInfo) {
        User user = userService.createWithPassword(
                dto.username(),
                dto.email(),
                dto.firstName(),
                dto.lastName(),
                dto.birthday(),
                dto.password()
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token to DB
        saveRefreshToken(user, refreshToken, deviceInfo);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(accessToken, refreshToken, expiresInSeconds);
    }

    @Transactional
    public JwtResponse login(LoginDTO dto, String deviceInfo) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save refresh token to DB
        saveRefreshToken(user, refreshToken, deviceInfo);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(accessToken, refreshToken, expiresInSeconds);
    }

    @Transactional
    public JwtResponse refreshToken(String oldRefreshToken, String deviceInfo) {
        // Find token in DB
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or has been revoked"));

        // Check if expired
        if (refreshTokenEntity.isExpired()) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = refreshTokenEntity.getUser();

        // Validate token signature
        if (!jwtService.isTokenValid(oldRefreshToken, user)) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Token rotation: delete old token
        refreshTokenRepository.delete(refreshTokenEntity);

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Save new refresh token
        saveRefreshToken(user, newRefreshToken, deviceInfo);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(newAccessToken, newRefreshToken, expiresInSeconds);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public void logoutAll(User currentUser) {
        refreshTokenRepository.deleteByUser(currentUser);
    }

    // Scheduled task: runs every Sunday at 00:00 (ONLY if the server is running)
    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = refreshTokenRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up {} expired refresh tokens", deletedCount);
    }

    private void saveRefreshToken(User user, String token, String deviceInfo) {
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(TimeUnit.MILLISECONDS.toSeconds(jwtService.getRefreshExpirationMs()));

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .deviceInfo(deviceInfo)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}