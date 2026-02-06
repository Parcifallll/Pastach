package com.example.Pastach.controller;

import com.example.Pastach.dto.auth.*;
import com.example.Pastach.model.User;
import com.example.Pastach.security.AuthService;
import com.example.Pastach.security.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Authentication",
        description = "User registration, login, token refresh and session management"
)
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns JWT tokens for authentication"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this username or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
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
    @Operation(
            summary = "User login",
            description = "Authenticates user and returns JWT tokens"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginDTO dto,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(authService.login(dto, deviceInfo, ipAddress));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Issues a new access token using a valid refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<JwtResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest refreshRequest,
            HttpServletRequest request
    ) {
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        return ResponseEntity.ok(authService.refreshToken(refreshRequest.refreshToken(), deviceInfo, ipAddress));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout from current session",
            description = "Revokes the provided refresh token, logging out the user from the current session"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully logged out"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            )
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @Operation(
            summary = "Logout from all sessions",
            description = "Revokes all refresh tokens for the current user, logging out from all devices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully logged out from all sessions"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal User currentUser) {
        authService.logoutAll(currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/sessions")
    @Operation(
            summary = "Get all active sessions",
            description = "Returns a list of all active sessions for the current user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of active sessions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SessionInfo.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<List<SessionInfo>> getMySessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(refreshTokenService.getActiveSessions(user.getId()));
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    @Operation(
            summary = "Revoke a specific session",
            description = "Revokes a specific session by its ID, logging out from that device"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Session successfully revoked"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Session not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "sessionId", description = "Session identifier to revoke", required = true, example = "a1b2c3d4e5f6")
    public ResponseEntity<Void> revokeSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal User user
    ) {
        refreshTokenService.revokeSession(user.getId(), sessionId);
        return ResponseEntity.noContent().build();
    }
}