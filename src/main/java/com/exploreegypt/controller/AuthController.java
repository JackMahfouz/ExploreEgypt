package com.exploreegypt.controller;

import com.exploreegypt.dto.AuthResponse;
import com.exploreegypt.dto.LoginRequest;
import com.exploreegypt.dto.RegisterRequest;
import com.exploreegypt.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/guest/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/guest/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(java.util.Map.of(
                "error", "Unauthorized",
                "message", "Authentication failed: " + e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of(
                "error", "Internal Server Error",
                "message", "An unexpected error occurred: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/guest/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader("Authorization");
        return authService.refreshToken(authHeader)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}

