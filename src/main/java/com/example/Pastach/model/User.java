package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class User {
    @NonNull
    @NotBlank
    @Email
    private String email;
    private String userName = "no_name";
    @Setter
    @NonNull
    @NotBlank
    private String id;

    @JsonFormat(pattern = "dd.MM.yyyy") // we should add a dependency
    private LocalDate birthday;
}

