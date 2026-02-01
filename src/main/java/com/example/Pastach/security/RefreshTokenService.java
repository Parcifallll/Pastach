package com.example.Pastach.security;

import com.example.Pastach.dto.auth.SessionInfo;
import com.example.Pastach.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    // SHA-256 hash
    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

    /**
     * Use Redis DB with two types of entries (String: Hash{} and String: Set{})
     * 1 - refresh_token:{refresh_token} -> {userId: {userId},
     * deviceInfo: {deviceInfo},
     * ipAddress: {ipAddress},
     * createdAt: {createdAt}}
     * 2 - user_tokens:{userId} -> {{user_token1}, {user_token_2}, {user_token_3}, ...}
     * <p>
     * It is vital that Set{} never invalidates, it is deleted only on /logout-all
     */
    public void saveRefreshToken(User user, String token, String deviceInfo, String ipAddress) {
        String tokenHash = hashToken(token);
        long ttlSeconds = TimeUnit.MILLISECONDS.toSeconds(jwtService.getRefreshExpirationMs());
        // 1 - add to Hash
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenHash;
        Map<String, String> tokenData = Map.of(
                "userId", user.getId().toString(),
                "deviceInfo", deviceInfo != null ? deviceInfo : "Unknown",
                "ipAddress", ipAddress != null ? ipAddress : "Unknown",
                "createdAt", Instant.now().toString()
        );
        redisTemplate.opsForHash().putAll(tokenKey, tokenData);
        redisTemplate.expire(tokenKey, ttlSeconds, TimeUnit.SECONDS);

        // 2 - add to Set
        String userTokensKey = USER_TOKENS_PREFIX + user.getId();
        redisTemplate.opsForSet().add(userTokensKey, tokenHash);
        redisTemplate.expire(userTokensKey, ttlSeconds, TimeUnit.SECONDS); // TTL from last added session

        log.info("Saved refresh token for user: {}", user.getUsername());
    }

    // O(1)-time
    public boolean exists(String token) {
        String tokenHash = hashToken(token);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    public Long getUserId(String token) {
        String tokenHash = hashToken(token);
        String key = REFRESH_TOKEN_PREFIX + tokenHash;
        Object userId = redisTemplate.opsForHash().get(key, "userId");
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    // logout from one device
    public void deleteRefreshToken(String token) {
        String tokenHash = hashToken(token);
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenHash;

        Object userIdObj = redisTemplate.opsForHash().get(tokenKey, "userId");

        if (userIdObj != null) {
            String userId = userIdObj.toString();

            // remove from user's token set
            String userTokensKey = USER_TOKENS_PREFIX + userId;
            redisTemplate.opsForSet().remove(userTokensKey, tokenHash);
        }

        redisTemplate.delete(tokenKey);
        log.debug("Deleted refresh token");
    }

    // logout from all sessions, takes O(n)-time
    public void logoutAll(Long userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(userTokensKey);

        if (tokens != null && !tokens.isEmpty()) {
            tokens.forEach(hash -> {
                String tokenKey = REFRESH_TOKEN_PREFIX + hash;
                redisTemplate.delete(tokenKey);
            });

            log.info("Deleted {} refresh tokens for user: {}", tokens.size(), userId);
        }

        redisTemplate.delete(userTokensKey);
    }

    // O(n)
    public List<SessionInfo> getActiveSessions(Long userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        Set<Object> tokenHashes = redisTemplate.opsForSet().members(userTokensKey);

        if (tokenHashes == null || tokenHashes.isEmpty()) {
            return List.of();
        }

        return tokenHashes.stream()
                .map(hash -> {
                    String tokenKey = REFRESH_TOKEN_PREFIX + hash;
                    Map<Object, Object> data = redisTemplate.opsForHash().entries(tokenKey);

                    if (data.isEmpty()) {
                        return null; // Token expired
                    }

                    return new SessionInfo(
                            hash.toString(),
                            Long.parseLong(data.get("userId").toString()),
                            data.get("deviceInfo").toString(),
                            data.get("ipAddress").toString(),
                            Instant.parse(data.get("createdAt").toString())
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void revokeSession(Long userId, String tokenHash) {
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenHash;

        redisTemplate.delete(tokenKey);

        String userTokensKey = USER_TOKENS_PREFIX + userId;
        redisTemplate.opsForSet().remove(userTokensKey, tokenHash);

        log.debug("Revoked session {} for user {}", tokenHash, userId);
    }

    public Long getActiveSessionsCount(Long userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        return redisTemplate.opsForSet().size(userTokensKey);
    }
}