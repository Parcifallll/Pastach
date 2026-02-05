package com.example.Pastach.controller;

import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
        name = "Admin",
        description = "Administration of user accounts: manage roles, lock/unlock accounts, delete users. Accessible to admins only."
)
public class AdminController {

    private final UserService userService;

    @PatchMapping("/{id}/roles")
    @Operation(
            summary = "Update user roles",
            description = "Replaces user’s roles with new roles. Accessible to admins only."
    )
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.updateRoles(id, roles));
    }

    @PatchMapping("/{id}/lock") // ?locked=true
    @Operation(
            summary = "Lock/unlock a user",
            description = "Sets the flag locked/unlocked. A locked user cannot create content and write comments."
    )
    public ResponseEntity<UserResponseDTO> toggleUserLock(
            @PathVariable Long id,
            @RequestParam boolean locked) {
        return ResponseEntity.ok(userService.toggleLock(id, locked));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(
            summary = "Delete a user",
            description = "Permanently deletes a user by ID."
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
