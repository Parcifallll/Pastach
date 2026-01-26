package com.example.Pastach.controller;

import com.example.Pastach.dto.auth.*;
import com.example.Pastach.model.User;
import com.example.Pastach.security.AuthService;
import com.example.Pastach.security.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(
            @Valid @RequestBody SignupDTO dto,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(dto, deviceInfo, ipAddress));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginDTO dto,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(authService.login(dto, deviceInfo, ipAddress));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshRequest,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(authService.refreshToken(refreshRequest.refreshToken(), deviceInfo, ipAddress));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal User currentUser) {
        authService.logoutAll(currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/sessions")
    public ResponseEntity<List<SessionInfo>> getMySessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(refreshTokenService.getActiveSessions(user.getId()));
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal User user
    ) {
        refreshTokenService.revokeSession(user.getId(), sessionId);
        return ResponseEntity.noContent().build();
    }
}