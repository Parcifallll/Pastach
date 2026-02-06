package com.example.Pastach.controller;

import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(
        name = "Admins' endpoints",
        description = "Administration of user accounts: manage roles, lock/unlock accounts and delete users. Accessible to admins only."
)
public class AdminController {

    private final UserService userService;

    @PatchMapping("/{id}/roles")
    @Operation(
            summary = "Update user roles",
            description = "Replaces the user's current set of roles with the provided list."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Roles updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid role's set",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "User identifier", required = true, example = "42")
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.updateRoles(id, roles));
    }

    @PatchMapping("/{id}/lock")
    @Operation(
            summary = "Lock/unlock a user",
            description = "Sets the user's lock status. Locked users cannot create new content (posts and comments)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lock status updated",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid lock status",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "User identifier", required = true, example = "42")
    @Parameter(name = "locked", description = "true = lock, false = unlock", required = true, example = "true")
    public ResponseEntity<UserResponseDTO> toggleUserLock(
            @PathVariable Long id,
            @RequestParam boolean locked) {
        return ResponseEntity.ok(userService.toggleLock(id, locked));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(
            summary = "Delete a user",
            description = "Permanently deletes a user account by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorHandler.ErrorResponse.class)))
    })
    @Parameter(name = "id", description = "User identifier to delete", required = true, example = "42")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
