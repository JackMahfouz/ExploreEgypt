package com.exploreegypt.controller;

import com.exploreegypt.dto.CafeRequest;
import com.exploreegypt.dto.CafeResponse;
import com.exploreegypt.entity.Cafe;
import com.exploreegypt.service.services.CafeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CafeController extends AbstractCurdController<CafeService, Cafe, CafeRequest, CafeResponse> {

    public CafeController(CafeService service) {
        super(service);
    }

    @Override
    protected Cafe toEntity(CafeRequest request) {
        return Cafe.builder()
                .name(request.getName())
                .address(request.getAddress())
                .hasWifi(request.getHasWifi())
                .build();
    }

    @Override
    protected CafeResponse toResponse(Cafe entity) {
        return CafeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .hasWifi(entity.getHasWifi())
                .build();
    }

    @GetMapping("/api/guest/cafes")
    public ResponseEntity<Iterable<CafeResponse>> getAllCafes() {
        return getAll();
    }

    @GetMapping("/api/guest/cafes/{id}")
    public ResponseEntity<CafeResponse> getCafeById(@PathVariable UUID id) {
        return getById(id);
    }

    @PostMapping("/api/cafes")
    public ResponseEntity<CafeResponse> createCafe(@RequestBody CafeRequest request) {
        return create(request);
    }

    @PutMapping("/api/cafes/{id}")
    public ResponseEntity<CafeResponse> updateCafe(@PathVariable UUID id, @RequestBody CafeRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/api/cafes/{id}")
    public ResponseEntity<Void> deleteCafe(@PathVariable UUID id) {
        return delete(id);
    }
}
