package com.example.Pastach.dto.mapper;

import com.example.Pastach.dto.reaction.ReactionCreateDTO;
import com.example.Pastach.dto.reaction.ReactionResponseDTO;
import com.example.Pastach.model.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetType", ignore = true)
    @Mapping(target = "targetId", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reaction toEntity(ReactionCreateDTO dto);

    ReactionResponseDTO toResponseDto(Reaction reaction);
}