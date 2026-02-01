package com.example.Pastach.dto.mapper;

import com.example.Pastach.dto.post.PostCreateDTO;
import com.example.Pastach.dto.post.PostResponseDTO;
import com.example.Pastach.dto.post.PostUpdateDTO;
import com.example.Pastach.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
// user do not update value (null) -> value is not updated (PATCH instead of PUT)
public interface PostMapper {
    Post toEntity(PostCreateDTO postCreateDTO);

    PostResponseDTO toResponseDto(Post post);

    @Mapping(target="comments", ignore=true)
    @Mapping(target="authorId", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    void updateFromDto(PostUpdateDTO postUpdateDTO, @MappingTarget Post post);

}