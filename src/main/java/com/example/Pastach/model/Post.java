package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class Post { // Post-entity
    @Setter
    private int id;
    private final String author;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private final LocalDateTime creationDate = LocalDateTime.now();
    @Setter
    private String text;
    @Setter
    private String photoUrl;
}
