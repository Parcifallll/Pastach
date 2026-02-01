package com.example.Pastach.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserCreateDTO(
        @NotBlank
        String username,

        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate birthday
) {
}