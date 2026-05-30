package com.exploreegypt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.exploreegypt.dto.AuthResponse;
import com.exploreegypt.dto.LoginRequest;
import com.exploreegypt.dto.RegisterRequest;
import com.exploreegypt.repository.TokenRepository;
import com.exploreegypt.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    void register() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());

        assertTrue(userRepository.findByEmail("test@example.com").isPresent());
    }

    @Test
    void login() throws Exception {
        com.exploreegypt.entity.User user = com.exploreegypt.entity.User.builder()
                .username("loginuser")
                .email("login@example.com")
                .password(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("login@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/guest/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    void logout() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("logoutuser")
                .email("logout@example.com")
                .password("password123")
                .build();

        String responseContent = mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        String accessToken = authResponse.getAccessToken();

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Verify that the token in the database is marked as expired and revoked
        com.exploreegypt.entity.Token token = tokenRepository.findByToken(accessToken).orElseThrow();
        assertTrue(token.isExpired());
        assertTrue(token.isRevoked());
    }

    @Test
    void refresh() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("refreshuser")
                .email("refresh@example.com")
                .password("password123")
                .build();

        String responseContent = mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        String refreshToken = authResponse.getRefreshToken();

        mockMvc.perform(post("/api/guest/refresh")
                .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }
}
