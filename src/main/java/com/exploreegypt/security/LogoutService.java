package com.exploreegypt.security;

import com.exploreegypt.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    public LogoutService(TokenRepository tokenRepository, TokenService tokenService) {
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String tokenValue;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        tokenValue = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(tokenValue)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            tokenService.evictToken(tokenValue);

            // Also revoke all other active tokens for the user
            var user = storedToken.getUser();
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            validUserTokens.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
                tokenService.evictToken(t.getToken());
            });
            tokenRepository.saveAll(validUserTokens);

            SecurityContextHolder.clearContext();
        }
    }
}
