package com.example.Pastach.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class Post { // Post-entity
    @Setter
    private Integer id;
    private final String author;
    private final Instant creationDate = Instant.now();
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
