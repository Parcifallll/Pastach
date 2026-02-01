package com.example.Pastach.dto.comment;


import lombok.Builder;

@Builder
public record CommentUpdateDTO(String text, String photoUrl) {
    public boolean hasContent() {
        return (text != null && !text.isBlank()) || (photoUrl != null && !photoUrl.isBlank());
    }
}
