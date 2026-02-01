package com.example.Pastach.dto.post;

import lombok.Builder;

@Builder
public record PostUpdateDTO(String text, String photoUrl) {
    public boolean hasContent() {
        return (text != null && !text.isBlank()) || (photoUrl != null && !photoUrl.isBlank());
    }
}