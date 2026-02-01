package com.example.Pastach.dto.comment;


import lombok.Builder;

@Builder
public record CommentCreateDTO(String text, String photoUrl) {

    public boolean hasContent() {
        return (text != null && !text.isBlank()) || (photoUrl != null && !photoUrl.isBlank());
    }
}
