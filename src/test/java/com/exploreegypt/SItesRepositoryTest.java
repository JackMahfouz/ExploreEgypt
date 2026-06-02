package com.exploreegypt;

import com.exploreegypt.entity.Site;
import com.exploreegypt.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Replaces @SpringBootTest for faster, automatically rolled-back tests
class SiteRepositoryTest { // Fixed typo in class name

    @Autowired
    private SiteRepository siteRepository;

    // Extracted constants for cleaner code
    private static final String SITE_NAME = "Test Site";
    private static final String SITE_ADDRESS = "123 Test St";

    // @BeforeEach and deleteAll() removed because @DataJpaTest handles rollbacks automatically

    @Test
    void contextLoads() {
        assertNotNull(siteRepository);
    }

    @Test
    void createSiteTest() {
        Site site = Site.builder()
                .name(SITE_NAME)
                .address(SITE_ADDRESS)
                .description("Test Description")
                .rate(new BigDecimal("100.00"))
                .build();

        Site savedSite = siteRepository.save(site);

        assertNotNull(savedSite.getId());
        assertEquals(SITE_NAME, savedSite.getName());
    }

    @Test
    void readSiteTest() {
        Site site = Site.builder()
                .name(SITE_NAME)
                .address(SITE_ADDRESS)
                .build();
        siteRepository.save(site);

        Site foundSite = siteRepository.findById(site.getId()).orElseThrow();

        // Switched to assertEquals for better failure messages and null-safety
        assertEquals(site.getId(), foundSite.getId());
        assertEquals(SITE_NAME, foundSite.getName());
    }

    @Test
    void updateSiteTest() {
        Site site = Site.builder()
                .name(SITE_NAME)
                .address(SITE_ADDRESS)
                .build();
        Site savedSite = siteRepository.save(site);

        savedSite.setName("Updated Name");
        siteRepository.save(savedSite);

        Site updatedSite = siteRepository.findById(savedSite.getId()).orElseThrow();

        assertEquals("Updated Name", updatedSite.getName());
    }

    @Test
    void deleteSiteTest() {
        Site site = Site.builder()
                .name(SITE_NAME)
                .address(SITE_ADDRESS)
                .build();
        Site savedSite = siteRepository.save(site);

        siteRepository.deleteById(savedSite.getId());

        assertFalse(siteRepository.findById(savedSite.getId()).isPresent());
    }
}