package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class User {
    @Setter
    @NonNull
    @NotBlank
    private String id;
    private String userName = "no_name";
    @NonNull
    @NotBlank
    @Email
    private String email;
    @JsonFormat(pattern = "dd.MM.yyyy") // we should add a dependency
    private LocalDate birthday;
}

