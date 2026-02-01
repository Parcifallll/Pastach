package com.example.Pastach.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record UserUpdateDTO(
        String username,
        @Email
        String email,

        String firstName,

        String lastName,

        LocalDate birthday
) {
}
