package com.example.Pastach.controller;

import com.example.Pastach.dto.post.PostCreateDTO;
import com.example.Pastach.dto.post.PostResponseDTO;
import com.example.Pastach.dto.post.PostUpdateDTO;
import com.example.Pastach.dto.reaction.ReactionCreateDTO;
import com.example.Pastach.model.ReactionTargetType;
import com.example.Pastach.model.User;
import com.example.Pastach.service.PostService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;


    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostCreateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(dto, currentUser.getId()));
    }

    @GetMapping
    public PagedModel<PostResponseDTO> getAllPosts(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

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
    public PagedModel<PostResponseDTO> getPostsByAuthorId(
            @PathVariable Long authorId,
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
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updateById(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.updateById(postId, dto, currentUser));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser) {
        postService.deleteById(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}/reactions")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @RequestBody ReactionCreateDTO dto,
            @AuthenticationPrincipal User user) {
        reactionService.toggleReaction(ReactionTargetType.POST, postId, dto.type(), user);
        return ResponseEntity.ok().build();
    }
}