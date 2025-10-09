package com.example.Pastach.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
public class Post { // Post-entity
    @Setter
    private Integer id;
    private final String author;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private final LocalDateTime creationDate = LocalDateTime.now();
    @Setter
    private String text;
    @Setter
    private String photoUrl;

    public Post(Integer id, String author, String text, String photoUrl) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.photoUrl = photoUrl;
    }


}
