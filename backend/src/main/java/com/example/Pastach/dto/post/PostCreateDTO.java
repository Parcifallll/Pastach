package com.example.Pastach.dto.post;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record PostCreateDTO(String text, String photoUrl) {

    public boolean hasContent() {
        return (text != null && !text.isBlank()) || (photoUrl != null && !photoUrl.isBlank());
    }
}

