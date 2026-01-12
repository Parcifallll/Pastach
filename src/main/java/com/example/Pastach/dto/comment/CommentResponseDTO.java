package com.example.Pastach.dto.comment;

import com.example.Pastach.model.Post;

import java.time.Instant;
import java.util.List;


import com.example.Pastach.model.Reaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public record CommentResponseDTO(Long id,
                                 String text,
                                 Long postId,
                                 String photoUrl,
                                 Long authorId,
                                 Instant createdAt,
                                 long likesCount,
                                 long dislikesCount) {
}
