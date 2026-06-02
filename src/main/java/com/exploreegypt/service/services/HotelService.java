package com.exploreegypt.service.services;

import com.exploreegypt.entity.Hotel;
import com.exploreegypt.repository.HotelRepository;
import org.springframework.stereotype.Service;

@Service
public class HotelService extends AbstractCurdService<HotelRepository, Hotel> {

    public HotelService(HotelRepository repo) {
        super(repo);
    }
}
