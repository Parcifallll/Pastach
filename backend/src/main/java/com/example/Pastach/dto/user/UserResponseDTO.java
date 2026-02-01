package com.example.Pastach.dto.user;

import lombok.Builder;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Builder
public record UserResponseDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        LocalDate birthday,
        boolean locked,
        Set<String> roles,
        Instant createdAt
) {
}