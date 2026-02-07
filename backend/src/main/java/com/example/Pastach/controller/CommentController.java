package com.example.Pastach.controller;

import com.example.Pastach.dto.comment.CommentCreateDTO;
import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.dto.comment.CommentUpdateDTO;
import com.example.Pastach.dto.reaction.ReactionCreateDTO;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.User;
import com.example.Pastach.service.CommentService;
import com.example.Pastach.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Comments",
        description = "Manage comments: create, read, update, delete comments and react to them"
)
public class CommentController {

    private final CommentService commentService;
    private final ReactionService reactionService;

    @PostMapping
    @Operation(
            summary = "Create a comment",
            description = "Creates a new comment on a post with text and/or photo. At least one of them must be provided."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment successfully created",
                    content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or no content provided",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is locked and cannot create comments",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "ID of the post to comment on", required = true, example = "1")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.create(postId, dto, user));
    }

    @GetMapping
    @Operation(
            summary = "Get all comments for a post",
            description = "Returns a paginated list of all comments for a specific post, sorted by creation date (newest first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "ID of the post", required = true, example = "1")
    public PagedModel<CommentResponseDTO> getAllByPostId(
            @PathVariable Long postId,
            @Parameter(description = "Pagination parameters: page number, size, sort")
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
    @Operation(
            summary = "Update a comment",
            description = "Updates comment content (text and/or photo). Only the comment author can update it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment successfully updated",
                    content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or no content provided",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the author or locked",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "ID of the post", required = true, example = "1")
    @Parameter(name = "commentId", description = "Comment identifier to update", required = true, example = "1")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.updateById(commentId, dto, user));
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "Delete a comment",
            description = "Permanently deletes a comment. Only the comment author or admin can delete it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Comment successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not the author or admin",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "ID of the post", required = true, example = "1")
    @Parameter(name = "commentId", description = "Comment identifier to delete", required = true, example = "1")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        commentService.deleteById(commentId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}/reactions")
    @Operation(
            summary = "React to a comment",
            description = "Add or remove a reaction (like/dislike) to a comment. If the same reaction already exists, it will be removed (toggle behavior)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reaction toggled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid reaction type",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "ID of the post", required = true, example = "1")
    @Parameter(name = "commentId", description = "Comment identifier to react to", required = true, example = "1")
    public ResponseEntity<Void> reactToComment(
            @PathVariable Long commentId,
            @RequestBody ReactionCreateDTO dto,
            @AuthenticationPrincipal User user) {
        reactionService.toggleReaction(ReactionTargetType.COMMENT, commentId, dto.type(), user);
        return ResponseEntity.ok().build();
    }
}