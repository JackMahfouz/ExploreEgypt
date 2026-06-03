package com.exploreegypt.controller;

import com.exploreegypt.dto.TransitStationRequest;
import com.exploreegypt.dto.TransitStationResponse;
import com.exploreegypt.entity.TransitStation;
import com.exploreegypt.service.services.TransitStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TransitStationController extends AbstractCurdController<TransitStationService, TransitStation, TransitStationRequest, TransitStationResponse> {

    public TransitStationController(TransitStationService service) {
        super(service);
    }

    @Override
    protected TransitStation toEntity(TransitStationRequest request) {
        return TransitStation.builder()
                .name(request.getName())
                .address(request.getAddress())
                .transitLine(request.getTransitLine())
                .build();
    }

    @Override
    protected TransitStationResponse toResponse(TransitStation entity) {
        return TransitStationResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .transitLine(entity.getTransitLine())
                .build();
    }

    @GetMapping("/api/guest/transit-stations")
    public ResponseEntity<Iterable<TransitStationResponse>> getAllTransitStations() {
        return getAll();
    }

    @GetMapping("/api/guest/transit-stations/{id}")
    public ResponseEntity<TransitStationResponse> getTransitStationById(@PathVariable UUID id) {
        return getById(id);
    }

    @PostMapping("/api/transit-stations")
    public ResponseEntity<TransitStationResponse> createTransitStation(@RequestBody TransitStationRequest request) {
        return create(request);
    }

    @PutMapping("/api/transit-stations/{id}")
    public ResponseEntity<TransitStationResponse> updateTransitStation(@PathVariable UUID id, @RequestBody TransitStationRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/api/transit-stations/{id}")
    public ResponseEntity<Void> deleteTransitStation(@PathVariable UUID id) {
        return delete(id);
    }
}
