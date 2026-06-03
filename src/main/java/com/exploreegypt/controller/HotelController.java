package com.exploreegypt.controller;

import com.exploreegypt.dto.HotelRequest;
import com.exploreegypt.dto.HotelResponse;
import com.exploreegypt.entity.Hotel;
import com.exploreegypt.service.services.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class HotelController extends AbstractCurdController<HotelService, Hotel, HotelRequest, HotelResponse> {

    public HotelController(HotelService service) {
        super(service);
    }

    @Override
    protected Hotel toEntity(HotelRequest request) {
        return Hotel.builder()
                .name(request.getName())
                .address(request.getAddress())
                .stars(request.getStars())
                .build();
    }

    @Override
    protected HotelResponse toResponse(Hotel entity) {
        return HotelResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .stars(entity.getStars())
                .build();
    }

    @GetMapping("/api/guest/hotels")
    public ResponseEntity<Iterable<HotelResponse>> getAllHotels() {
        return getAll();
    }

    @GetMapping("/api/guest/hotels/{id}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable UUID id) {
        return getById(id);
    }

    @PostMapping("/api/hotels")
    public ResponseEntity<HotelResponse> createHotel(@RequestBody HotelRequest request) {
        return create(request);
    }

    @PutMapping("/api/hotels/{id}")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable UUID id, @RequestBody HotelRequest request) {
        return update(id, request);
    }

    @DeleteMapping("/api/hotels/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable UUID id) {
        return delete(id);
    }
}
