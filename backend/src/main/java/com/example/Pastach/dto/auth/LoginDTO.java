package com.example.Pastach.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginDTO(
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}