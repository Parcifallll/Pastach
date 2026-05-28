package com.example.Pastach.controller;

import com.example.Pastach.dto.post.PostCreateDTO;
import com.example.Pastach.dto.post.PostResponseDTO;
import com.example.Pastach.dto.post.PostUpdateDTO;
import com.example.Pastach.dto.reaction.ReactionCreateDTO;
import com.example.Pastach.dto.recommendation.RecommendationViewReportDTO;
import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.PostRepository;
import com.example.Pastach.service.PostService;
import com.example.Pastach.service.ReactionService;
import com.example.Pastach.service.RecommendationAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Posts",
        description = "Manage posts: create, read, update, delete posts and react to them"
)
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;
    private final RecommendationAnalyticsService recommendationAnalyticsService;

    @PostMapping
    @Operation(
            summary = "Create a new post",
            description = "Creates a new post with text and/or photo. At least one of them must be provided."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Post successfully created",
                    content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
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
                    description = "User is locked and cannot create posts",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostCreateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(dto, currentUser.getId()));
    }

    @GetMapping
    @SecurityRequirements({})
    @Operation(
            summary = "Get all posts",
            description = "Returns a paginated list of all posts sorted by creation date (newest first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
            )
    })
    public PagedModel<PostResponseDTO> getAllPosts(
            @Parameter(description = "Pagination parameters: page number, size, sort")
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {

        Page<PostResponseDTO> page = postService.getAll(pageable);

        PagedModel<PostResponseDTO> pagedModel = PagedModel.of(
                page.getContent(),
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );

        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PostController.class).getAllPosts(pageable)
        ).withSelfRel();
        pagedModel.add(selfLink);

        if (page.hasNext()) {
            Link nextLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PostController.class).getAllPosts(page.nextPageable())
            ).withRel("next");
            pagedModel.add(nextLink);
        }

        if (page.hasPrevious()) {
            Link prevLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PostController.class).getAllPosts(page.previousPageable())
            ).withRel("prev");
            pagedModel.add(prevLink);
        }

        return pagedModel;
    }

    @GetMapping("/users/{authorId}/posts")
    @SecurityRequirements({})
    @Operation(
            summary = "Get posts by author",
            description = "Returns a paginated list of posts created by a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "authorId", description = "Author's user ID", required = true, example = "1")
    public PagedModel<PostResponseDTO> getPostsByAuthorId(
            @PathVariable Long authorId,
            @Parameter(description = "Pagination parameters: page number, size, sort")
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDTO> page = postService.getByAuthorId(authorId, pageable);

        PagedModel<PostResponseDTO> pagedModel = PagedModel.of(
                page.getContent(),
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );

        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PostController.class).getPostsByAuthorId(authorId, pageable)
        ).withSelfRel();
        pagedModel.add(selfLink);

        if (page.hasNext()) {
            Link nextLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PostController.class).getPostsByAuthorId(authorId, page.nextPageable())
            ).withRel("next");
            pagedModel.add(nextLink);
        }

        if (page.hasPrevious()) {
            Link prevLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PostController.class).getPostsByAuthorId(authorId, page.previousPageable())
            ).withRel("prev");
            pagedModel.add(prevLink);
        }

        return pagedModel;
    }

    @GetMapping("/{id}")
    @SecurityRequirements({})
    @Operation(
            summary = "Get post by ID",
            description = "Returns a single post by its identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post found",
                    content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "id", description = "Post identifier", required = true, example = "1")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PatchMapping("/{postId}")
    @Operation(
            summary = "Update a post",
            description = "Updates post content (text and/or photo). Only the post author can update it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post successfully updated",
                    content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
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
                    description = "User is not the author or is locked",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "Post identifier to update", required = true, example = "1")
    public ResponseEntity<PostResponseDTO> updateById(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.updateById(postId, dto, currentUser));
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "Delete a post",
            description = "Permanently deletes a post. Only the post author or admin can delete it."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Post successfully deleted"
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
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "Post identifier to delete", required = true, example = "1")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser) {
        postService.deleteById(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}/reactions")
    @Operation(
            summary = "React to a post",
            description = "Add or remove a reaction (like/dislike) to a post. If the same reaction already exists, it will be removed (toggle behavior)."
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
                    description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "postId", description = "Post identifier to react to", required = true, example = "1")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @RequestBody ReactionCreateDTO dto,
            @AuthenticationPrincipal User user) {
        reactionService.toggleReaction(ReactionTargetType.POST, postId, dto.type(), user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommendations")
    @Operation(
            summary = "Get recommended posts",
            description = "Returns personalized post recommendations for the current user based on their activity. Recommendations are created in Python microservice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recommendations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PostResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "limit", description = "Max number of recommendations to return", example = "10")
    @Parameter(name = "offset", description = "Offset for pagination", example = "0")
    @PreAuthorize("isAuthenticated()")
    public CompletableFuture<ResponseEntity<PagedModel<PostResponseDTO>>> getRecommendedPosts(  // PagedModel (HATEOAS)
                                                                                                @RequestParam(defaultValue = "10") int limit,
                                                                                                @RequestParam(defaultValue = "0") int offset,
                                                                                                @AuthenticationPrincipal User currentUser) {

        Long userId = currentUser.getId();
        return postService.getRecommendedPosts(userId, limit, offset)
                .thenApply(posts -> {
                    PagedModel<PostResponseDTO> pagedModel = PagedModel.of(posts, new PagedModel.PageMetadata(limit, offset / limit, posts.size() + offset));  // metadata
                    pagedModel.add(Link.of("/posts/recommendations?limit=" + limit + "&offset=" + (offset + limit)).withRel("next"));  // next link
                    pagedModel.add(Link.of("/posts/recommendations?limit=" + limit + "&offset=" + offset).withSelfRel());  // self
                    return ResponseEntity.ok(pagedModel);
                })
                .exceptionally(e -> {
                    log.error("Controller error: ", e);
                    return ResponseEntity.ok(PagedModel.empty());  // Fallback empty with HATEOAS
                });
    }

    @PostMapping("/recommendations/{postId}/view")
    @Operation(
            summary = "Report post view in recommendations",
            description = "Record user viewed a recommended post with view duration for analytics"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "View recorded successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found"
            )
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportRecommendationView(
            @PathVariable Long postId,
            @RequestBody RecommendationViewReportDTO dto,
            @AuthenticationPrincipal User currentUser) {

        recommendationAnalyticsService.logRecommendationViewed(
                currentUser.getId(),
                postId,
                dto.authorId(),
                dto.viewedAt(),
                dto.createdAt(),
                dto.viewDuration()
        );

        return ResponseEntity.noContent().build();
    }
}