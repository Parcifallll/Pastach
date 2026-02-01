package com.example.Pastach.controller;

import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.dto.reaction.ReactionCreateDTO;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.User;
import com.example.Pastach.service.CommentService;
import com.example.Pastach.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.create(postId, dto, user));
    }

    @GetMapping
    public PagedModel<CommentResponseDTO> getAllByPostId(
            @PathVariable Long postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CommentResponseDTO> page = commentService.getAllByPostId(postId, pageable);

        PagedModel<CommentResponseDTO> pagedModel = PagedModel.of(
                page.getContent(),
                new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages())
        );

        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(CommentController.class).getAllByPostId(postId, pageable)
        ).withSelfRel();
        pagedModel.add(selfLink);

        if (page.hasNext()) {
            Link nextLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(CommentController.class).getAllByPostId(postId, page.nextPageable())
            ).withRel("next");
            pagedModel.add(nextLink);
        }

        if (page.hasPrevious()) {
            Link prevLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(CommentController.class).getAllByPostId(postId, page.previousPageable())
            ).withRel("prev");
            pagedModel.add(prevLink);
        }

        return pagedModel;
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.updateById(commentId, dto, user));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        commentService.deleteById(commentId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}/reactions")
    public ResponseEntity<Void> reactToComment(
            @PathVariable Long commentId,
            @RequestBody ReactionCreateDTO dto,
            @AuthenticationPrincipal User user) {
        reactionService.toggleReaction(ReactionTargetType.COMMENT, commentId, dto.type(), user);
        return ResponseEntity.ok().build();
    }
}