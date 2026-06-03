package com.exploreegypt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.exploreegypt.dto.*;
import com.exploreegypt.entity.*;
import com.exploreegypt.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllersTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private TransitStationRepository transitStationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
        cafeRepository.deleteAll();
        hotelRepository.deleteAll();
        siteRepository.deleteAll();
        transitStationRepository.deleteAll();
    }

    private String getAuthHeader() throws Exception {
        String uniqueEmail = "user_" + UUID.randomUUID() + "@example.com";
        String uniqueUsername = "user_" + UUID.randomUUID().toString().substring(0, 8);
        RegisterRequest request = RegisterRequest.builder()
                .username(uniqueUsername)
                .email(uniqueEmail)
                .password("password123")
                .build();

        String responseContent = mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseContent, AuthResponse.class);
        return "Bearer " + authResponse.getAccessToken();
    }

    @Test
    void testCafeEndpoints() throws Exception {
        // 1. Create a cafe in the database directly to test GET
        Cafe cafe = Cafe.builder()
                .name("Al-Fishawy")
                .address("Khan el-Khalili, Cairo")
                .hasWifi(true)
                .build();
        Cafe savedCafe = cafeRepository.save(cafe);
        UUID id = savedCafe.getId();

        // 2. Test Guest GET all
        mockMvc.perform(get("/api/guest/cafes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Al-Fishawy"))
                .andExpect(jsonPath("$[0].address").value("Khan el-Khalili, Cairo"))
                .andExpect(jsonPath("$[0].hasWifi").value(true));

        // 3. Test Guest GET by ID
        mockMvc.perform(get("/api/guest/cafes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Al-Fishawy"));

        // 4. Test Guest POST (should be forbidden)
        CafeRequest requestDto = CafeRequest.builder()
                .name("El-Tahrir Cafe")
                .address("Tahrir Square, Cairo")
                .hasWifi(false)
                .build();

        mockMvc.perform(post("/api/cafes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        // 5. Test Guest PUT (should be forbidden)
        mockMvc.perform(put("/api/cafes/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        // 6. Test Guest DELETE (should be forbidden)
        mockMvc.perform(delete("/api/cafes/" + id))
                .andExpect(status().isForbidden());

        // Authenticate
        String authHeader = getAuthHeader();

        // 7. Test Auth POST
        String createResponse = mockMvc.perform(post("/api/cafes")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("El-Tahrir Cafe"))
                .andExpect(jsonPath("$.hasWifi").value(false))
                .andReturn().getResponse().getContentAsString();

        CafeResponse createdCafeResponse = objectMapper.readValue(createResponse, CafeResponse.class);
        UUID newCafeId = createdCafeResponse.getId();

        // 8. Test Auth PUT (Update)
        CafeRequest updateRequestDto = CafeRequest.builder()
                .name("El-Tahrir Cafe Updated")
                .address("Tahrir Square, Cairo")
                .hasWifi(true)
                .build();

        mockMvc.perform(put("/api/cafes/" + newCafeId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("El-Tahrir Cafe Updated"))
                .andExpect(jsonPath("$.hasWifi").value(true));

        // Verify database update
        Cafe updatedCafe = cafeRepository.findById(newCafeId).orElseThrow();
        assertEquals("El-Tahrir Cafe Updated", updatedCafe.getName());
        assertTrue(updatedCafe.getHasWifi());

        // 9. Test Auth DELETE
        mockMvc.perform(delete("/api/cafes/" + newCafeId)
                .header("Authorization", authHeader))
                .andExpect(status().isNoContent());

        assertFalse(cafeRepository.existsById(newCafeId));
    }

    @Test
    void testHotelEndpoints() throws Exception {
        Hotel hotel = Hotel.builder()
                .name("Steigenberger Cecil")
                .address("Alexandria")
                .stars(5)
                .build();
        Hotel savedHotel = hotelRepository.save(hotel);
        UUID id = savedHotel.getId();

        // Guest GET
        mockMvc.perform(get("/api/guest/hotels/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Steigenberger Cecil"))
                .andExpect(jsonPath("$.stars").value(5));

        // Guest POST (Forbidden)
        HotelRequest request = HotelRequest.builder()
                .name("Hilton")
                .address("Luxor")
                .stars(5)
                .build();
        mockMvc.perform(post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Authenticate & POST
        String authHeader = getAuthHeader();
        mockMvc.perform(post("/api/hotels")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hilton"));
    }

    @Test
    void testSiteEndpoints() throws Exception {
        Site site = Site.builder()
                .name("Giza Pyramids")
                .address("Giza")
                .description("The Great Pyramids")
                .rate(new java.math.BigDecimal("200.00"))
                .build();
        Site savedSite = siteRepository.save(site);
        UUID id = savedSite.getId();

        // Guest GET
        mockMvc.perform(get("/api/guest/sites/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Giza Pyramids"))
                .andExpect(jsonPath("$.rate").value(200.00));

        // Guest POST (Forbidden)
        SiteRequest request = SiteRequest.builder()
                .name("Karnak Temple")
                .address("Luxor")
                .description("Ancient Egyptian Temple")
                .rate(new java.math.BigDecimal("150.00"))
                .build();
        mockMvc.perform(post("/api/sites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Authenticate & POST
        String authHeader = getAuthHeader();
        mockMvc.perform(post("/api/sites")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Karnak Temple"));
    }

    @Test
    void testTransitStationEndpoints() throws Exception {
        TransitStation station = TransitStation.builder()
                .name("Sadat Metro")
                .address("Tahrir Square, Cairo")
                .transitLine("Line 1 & 2")
                .build();
        TransitStation savedStation = transitStationRepository.save(station);
        UUID id = savedStation.getId();

        // Guest GET
        mockMvc.perform(get("/api/guest/transit-stations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sadat Metro"))
                .andExpect(jsonPath("$.transitLine").value("Line 1 & 2"));

        // Guest POST (Forbidden)
        TransitStationRequest request = TransitStationRequest.builder()
                .name("Shuhada Metro")
                .address("Ramses Square, Cairo")
                .transitLine("Line 1 & 2")
                .build();
        mockMvc.perform(post("/api/transit-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Authenticate & POST
        String authHeader = getAuthHeader();
        mockMvc.perform(post("/api/transit-stations")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Shuhada Metro"));
    }
}
