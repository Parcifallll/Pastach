package com.example.Pastach.controller;

import com.example.Pastach.dto.user.PasswordChangeDTO;
import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.dto.user.UserUpdateDTO;
import com.example.Pastach.model.User;
import com.example.Pastach.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrent(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getById(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public PagedModel<UserResponseDTO> getAll(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {

        Page<UserResponseDTO> page = userService.getAll(pageable);

        PagedModel<UserResponseDTO> pagedModel = PagedModel.of(
                page.getContent(),
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );

        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getAll(pageable)
        ).withSelfRel();
        pagedModel.add(selfLink);

        if (page.hasNext()) {
            Link nextLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(UserController.class).getAll(page.nextPageable())
            ).withRel("next");
            pagedModel.add(nextLink);
        }

        if (page.hasPrevious()) {
            Link prevLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(UserController.class).getAll(page.previousPageable())
            ).withRel("prev");
            pagedModel.add(prevLink);
        }

        return pagedModel;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.updateById(id, dto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDTO dto,
            @AuthenticationPrincipal User currentUser) {
        userService.changePassword(id, dto, currentUser);
        return ResponseEntity.noContent().build();
    }
}