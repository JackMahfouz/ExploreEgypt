package com.exploreegypt.security;

import com.exploreegypt.entity.Token;
import com.exploreegypt.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public TokenService(
            TokenRepository tokenRepository,
            @Value("${application.security.jwt.expiration:86400000}") long jwtExpiration,
            @Value("${application.security.jwt.refresh-token.expiration:604800000}") long refreshExpiration
    ) {
        this.tokenRepository = tokenRepository;
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    @Cacheable(cacheNames = "tokens", key = "#tokenValue")
    public Optional<Token> findByToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue);
    }

    @CacheEvict(cacheNames = "tokens", key = "#tokenValue")
    public void evictToken(String tokenValue) {
        // Automatically evicts from cache
    }

    @CacheEvict(cacheNames = "tokens", allEntries = true)
    public void clearTokenCache() {
        // Automatically clears the entire tokens cache
    }

    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshExpiration);
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        long expiryTime = System.currentTimeMillis() + expiration;
        return uuid + "." + expiryTime;
    }

    public String extractUsername(String tokenValue) {
        return findByToken(tokenValue)
                .map(t -> t.getUser().getEmail())
                .orElse(null);
    }

    public boolean isTokenValid(String tokenValue, UserDetails userDetails) {
        String username = extractUsername(tokenValue);
        return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(tokenValue);
    }

    public boolean isTokenExpired(String tokenValue) {
        if (tokenValue == null) {
            return true;
        }
        try {
            String[] parts = tokenValue.split("\\.");
            if (parts.length != 2) {
                return true;
            }
            long expiryTime = Long.parseLong(parts[1]);
            return System.currentTimeMillis() > expiryTime;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
