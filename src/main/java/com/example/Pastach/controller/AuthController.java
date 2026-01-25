package com.example.Pastach.controller;

import com.example.Pastach.dto.auth.JwtResponse;
import com.example.Pastach.dto.auth.LoginDTO;
import com.example.Pastach.dto.auth.RefreshTokenRequest;
import com.example.Pastach.dto.auth.SignupDTO;
import com.example.Pastach.model.User;
import com.example.Pastach.security.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(
            @Valid @RequestBody SignupDTO dto,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(dto, deviceInfo));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginDTO dto,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        return ResponseEntity.ok(authService.login(dto, deviceInfo));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshRequest,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        return ResponseEntity.ok(authService.refreshToken(refreshRequest.refreshToken(), deviceInfo));
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
}