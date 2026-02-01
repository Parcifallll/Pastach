package com.example.Pastach.dto.reaction;

import com.example.Pastach.model.ReactionType;
import jakarta.validation.constraints.NotNull;

public record ReactionCreateDTO(
        @NotNull(message = "Reaction type is required")
        ReactionType type
) {}