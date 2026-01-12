package com.example.Pastach.service;

import com.example.Pastach.dto.mapper.UserMapper;
import com.example.Pastach.dto.user.PasswordChangeDTO;
import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.dto.user.UserUpdateDTO;
import com.example.Pastach.exception.IncorrectParameterException;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.exception.UserNotFoundException;
import com.example.Pastach.model.Role;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.RoleRepository;
import com.example.Pastach.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createWithPassword(String username, String email, String firstName,
                                   String lastName, LocalDate birthday, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException("User with email " + email + " already exists");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistException("User with username '" + username + "' already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthday(birthday);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Default role: USER
        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER not found in database"));
        user.setRoles(new HashSet<>(Set.of(userRole)));

        return userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::toResponseDto);
    }


    @PreAuthorize("isAuthenticated()")
    @Transactional
    public UserResponseDTO updateById(Long userId, UserUpdateDTO dto, @AuthenticationPrincipal User currentUser) {
        if (!userId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (dto.username() != null) {
            String trimmed = dto.username().trim();
            if (trimmed.isEmpty()) {
                throw new IncorrectParameterException("Username cannot be blank");
            }
            if (userRepository.existsByUsernameAndIdNot(dto.username(), userId)) {
                throw new UserAlreadyExistException("User with username '" + dto.username() + "' already exists");
            }
        }

        if (dto.email() != null) {
            if (userRepository.existsByEmailAndIdNot(dto.email(), userId)) {
                throw new UserAlreadyExistException("User with email " + dto.email() + " already exists");
            }
        }

        userMapper.updateFromDto(dto, user);
        user = userRepository.save(user);
        return userMapper.toResponseDto(user);
    }


    @Transactional
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public void deleteById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }


    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void changePassword(Long userId, PasswordChangeDTO dto, @AuthenticationPrincipal User currentUser) {
        if (!userId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only change your own password");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    // admin only
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO updateRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Set<Role> roles = roleNames.stream()
                .map(name -> RoleEnum.valueOf(name.toUpperCase()))
                .map(roleEnum -> roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user = userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO toggleLock(Long userId, boolean locked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setLocked(locked);
        user = userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

}
