package com.exploreegypt.service;

import com.exploreegypt.dto.AuthResponse;
import com.exploreegypt.dto.LoginRequest;
import com.exploreegypt.dto.RegisterRequest;
import com.exploreegypt.entity.Token;
import com.exploreegypt.entity.TokenType;
import com.exploreegypt.entity.User;
import com.exploreegypt.repository.TokenRepository;
import com.exploreegypt.repository.UserRepository;
import com.exploreegypt.security.CustomUserDetails;
import com.exploreegypt.security.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public Optional<AuthResponse> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent() || 
            userRepository.findByUsername(request.getUsername()).isPresent()) {
            return Optional.empty();
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        var savedUser = userRepository.save(user);

        var customUser = new CustomUserDetails(user);
        var accessToken = tokenService.generateToken(customUser);
        var refreshToken = tokenService.generateRefreshToken(customUser);
        
        saveUserToken(savedUser, accessToken, TokenType.BEARER);
        saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

        return Optional.of(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
                
        var customUser = new CustomUserDetails(user);
        var accessToken = tokenService.generateToken(customUser);
        var refreshToken = tokenService.generateRefreshToken(customUser);
        
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken, TokenType.BEARER);
        saveUserToken(user, refreshToken, TokenType.REFRESH);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public Optional<AuthResponse> refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String refreshToken = authHeader.substring(7);
        
        var storedRefreshTokenOpt = tokenRepository.findByToken(refreshToken)
                .filter(t -> t.getTokenType() == TokenType.REFRESH 
                        && !t.isExpired() 
                        && !t.isRevoked() 
                        && !tokenService.isTokenExpired(refreshToken));
                        
        if (storedRefreshTokenOpt.isPresent()) {
            var storedRefreshToken = storedRefreshTokenOpt.get();
            var user = storedRefreshToken.getUser();
            var customUser = new CustomUserDetails(user);
            
            var accessToken = tokenService.generateToken(customUser);
            revokeAllUserAccessTokens(user);
            saveUserToken(user, accessToken, TokenType.BEARER);
            
            return Optional.of(AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());
        }
        return Optional.empty();
    }

    private void saveUserToken(User user, String tokenValue, TokenType tokenType) {
        var token = Token.builder()
                .user(user)
                .token(tokenValue)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            tokenService.evictToken(token.getToken());
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllUserAccessTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.stream()
                .filter(token -> token.getTokenType() == TokenType.BEARER)
                .forEach(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                    tokenService.evictToken(token.getToken());
                });
        tokenRepository.saveAll(validUserTokens);
    }
}
