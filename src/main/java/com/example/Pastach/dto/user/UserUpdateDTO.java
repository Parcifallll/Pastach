package com.example.Pastach.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record UserUpdateDTO(
        @NotBlank
        String username,

        @NotBlank @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate birthday
) {
}
