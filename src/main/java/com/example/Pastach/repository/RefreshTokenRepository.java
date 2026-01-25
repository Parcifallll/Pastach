package com.example.Pastach.repository;

import com.example.Pastach.model.RefreshToken;
import com.example.Pastach.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUser(User user);
    
    void deleteByToken(String token);
    
    @Modifying
    int deleteByExpiresAtBefore(LocalDateTime dateTime);
}