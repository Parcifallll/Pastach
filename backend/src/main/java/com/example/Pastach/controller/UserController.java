package com.example.Pastach.controller;

import com.example.Pastach.dto.user.PasswordChangeDTO;
import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.dto.user.UserUpdateDTO;
import com.example.Pastach.model.User;
import com.example.Pastach.service.UserService;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "User profile management: view, update, delete user profiles and change passwords"
)
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile information of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    public ResponseEntity<UserResponseDTO> getCurrent(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getById(currentUser.getId()));
    }

    @GetMapping("/{id}")
    @SecurityRequirements({})
    @Operation(
            summary = "Get user by ID",
            description = "Returns the profile information of a user by their ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "id", description = "User identifier", required = true, example = "1")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @SecurityRequirements({})
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all users sorted by creation date (newest first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
            )
    })
    public PagedModel<UserResponseDTO> getAll(
            @Parameter(description = "Pagination parameters: page number, size, sort")
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
    @Operation(
            summary = "Update user profile",
            description = "Updates user profile information. Users can only update their own profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User cannot update another user's profile",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "id", description = "User identifier to update", required = true, example = "1")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.updateById(id, dto, currentUser));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user account",
            description = "Permanently deletes a user account. Only admins can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions (admin only)",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "id", description = "User identifier to delete", required = true, example = "1")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @Operation(
            summary = "Change user password",
            description = "Changes the password for a user. Users can only change their own password. Requires the current password for verification."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or incorrect current password",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User cannot change another user's password",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))
            )
    })
    @Parameter(name = "id", description = "User identifier", required = true, example = "1")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDTO dto,
            @AuthenticationPrincipal User currentUser) {
        userService.changePassword(id, dto, currentUser);
        return ResponseEntity.noContent().build();
    }
}