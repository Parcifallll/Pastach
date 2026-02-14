package com.example.Pastach.security;

import com.example.Pastach.dto.auth.JwtResponse;
import com.example.Pastach.dto.auth.LoginDTO;
import com.example.Pastach.dto.auth.SignupDTO;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.UserRepository;
import com.example.Pastach.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse signup(SignupDTO dto, String deviceInfo, String ipAddress) {
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

        // save refresh token to Redis
        refreshTokenService.saveRefreshToken(user, refreshToken, deviceInfo, ipAddress);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(accessToken, refreshToken, expiresInSeconds);
    }

    @Transactional
    public JwtResponse login(LoginDTO dto, String deviceInfo, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // save refresh token to Redis
        refreshTokenService.saveRefreshToken(user, refreshToken, deviceInfo, ipAddress);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(accessToken, refreshToken, expiresInSeconds);
    }

    @Transactional
    public JwtResponse refreshToken(String oldRefreshToken, String deviceInfo, String ipAddress) {
        // takes O(1)-time
        if (!refreshTokenService.exists(oldRefreshToken)) {
            throw new IllegalArgumentException("Refresh token not found or has been revoked");
        }

        Long userId = refreshTokenService.getUserId(oldRefreshToken);
        if (userId == null) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // validate token signature
        if (!jwtService.isTokenValid(oldRefreshToken, user)) {
            refreshTokenService.deleteRefreshToken(oldRefreshToken);
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // token rotation: delete old token
        refreshTokenService.deleteRefreshToken(oldRefreshToken);

        // generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user, newRefreshToken, deviceInfo, ipAddress);
        log.info("{} refreshed token", userId);

        long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessExpirationMs());
        return new JwtResponse(newAccessToken, newRefreshToken, expiresInSeconds);
    }

    // takes O(1)-time
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
        log.info("User logged out, refresh token revoked");
    }

    // takes O(n)-time
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void logoutAll(User currentUser) {
        refreshTokenService.logoutAll(currentUser.getId());
        log.info("User {} logged out from all devices", currentUser.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    public Long getActiveSessionsCount(User currentUser) {
        return refreshTokenService.getActiveSessionsCount(currentUser.getId());
    }
}