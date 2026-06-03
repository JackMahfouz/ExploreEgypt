package com.exploreegypt.controller;

import com.exploreegypt.dto.SiteRequest;
import com.exploreegypt.dto.SiteResponse;
import com.exploreegypt.entity.Site;
import com.exploreegypt.service.services.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SiteController extends AbstractCurdController<SiteService, Site, SiteRequest, SiteResponse> {

    public SiteController(SiteService service) {
        super(service);
    }

    @Override
    protected Site toEntity(SiteRequest request) {
        return Site.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .rate(request.getRate())
                .build();
    }

    @Override
    protected SiteResponse toResponse(Site entity) {
        return SiteResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .rate(entity.getRate())
                .build();
    }

    @GetMapping("/api/guest/sites")
    public ResponseEntity<Iterable<SiteResponse>> getAllSites() {
        return getAll();
    }

    @GetMapping("/api/guest/sites/{id}")
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable UUID id) {
        return getById(id);
    }

    @PostMapping("/api/sites")
    public ResponseEntity<SiteResponse> createSite(@RequestBody SiteRequest request) {
        return create(request);
    }

    @PutMapping("/api/sites/{id}")
    public ResponseEntity<SiteResponse> updateSite(@PathVariable UUID id, @RequestBody SiteRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/api/sites/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable UUID id) {
        return delete(id);
    }
}
