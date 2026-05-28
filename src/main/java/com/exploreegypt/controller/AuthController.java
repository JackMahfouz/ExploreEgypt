package com.exploreegypt.controller;

import com.exploreegypt.dto.AuthResponse;
import com.exploreegypt.dto.LoginRequest;
import com.exploreegypt.dto.RegisterRequest;
import com.exploreegypt.entity.Token;
import com.exploreegypt.entity.TokenType;
import com.exploreegypt.entity.User;
import com.exploreegypt.repository.TokenRepository;
import com.exploreegypt.repository.UserRepository;
import com.exploreegypt.security.CustomUserDetails;
import com.exploreegypt.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent() || 
            userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        var savedUser = userRepository.save(user);

        var customUser = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(customUser);
        var refreshToken = jwtService.generateRefreshToken(customUser);
        
        saveUserToken(savedUser, jwtToken);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
                
        var customUser = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(customUser);
        var refreshToken = jwtService.generateRefreshToken(customUser);
        
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            var customUser = new CustomUserDetails(user);
            if (jwtService.isTokenValid(refreshToken, customUser)) {
                var accessToken = jwtService.generateToken(customUser);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return ResponseEntity.ok(AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
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
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
