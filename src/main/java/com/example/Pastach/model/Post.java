package com.example.Pastach.model;

import java.time.Instant;

public class Post { // Post-entity

    private final String author;
    private final Instant creationDate = Instant.now();
    private String text;
    private String photoUrl;

    public Post(String author, String text, String photoUrl) {
        this.author = author;
        this.text = text;
        this.photoUrl = photoUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setText(String text) {
        this.text = text;
    }
}
