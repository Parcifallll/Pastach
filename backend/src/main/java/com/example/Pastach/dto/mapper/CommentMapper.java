package com.example.Pastach.dto.mapper;


import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "id", ignore = true) //target has a field, but source does not
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    Comment toEntity(CommentCreateDTO commentCreateDTO);

    @Mapping(target = "postId", source = "post.id")
    CommentResponseDTO toResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    void updateFromDto(CommentUpdateDTO commentUpdateDTO, @MappingTarget Comment comment);
}
