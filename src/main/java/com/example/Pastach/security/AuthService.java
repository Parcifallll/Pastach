package com.example.Pastach.security;

import com.example.Pastach.dto.auth.JwtResponse;
import com.example.Pastach.dto.auth.LoginDTO;
import com.example.Pastach.dto.auth.SignupDTO;
import com.example.Pastach.exception.UserAlreadyExistException;
import com.example.Pastach.model.User;
import com.example.Pastach.repository.UserRepository;
import com.example.Pastach.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse signup(SignupDTO dto) {
        if (userRepository.existsById(dto.id())) {
            throw new UserAlreadyExistException("User with id '" + dto.id() + "' already exists");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistException("User with email '" + dto.email() + "' already exists");
        }

        User user = userService.createWithPassword(
                dto.id(),
                dto.email(),
                dto.firstName(),
                dto.lastName(),
                dto.birthday(),
                dto.password()
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public JwtResponse login(LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public JwtResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return new JwtResponse(newAccessToken, refreshToken);
    }
}