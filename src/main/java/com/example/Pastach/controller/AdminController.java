package com.example.Pastach.controller;

import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserResponseDTO> updateUserRoles(
            @PathVariable String id,
            @RequestBody Set<String> roles) {
        return ResponseEntity.ok(userService.updateRoles(id, roles));
    }

    @PatchMapping("/{id}/lock") //?locked=true
    public ResponseEntity<UserResponseDTO> toggleUserLock(
            @PathVariable String id,
            @RequestParam boolean locked) {
        return ResponseEntity.ok(userService.toggleLock(id, locked));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}