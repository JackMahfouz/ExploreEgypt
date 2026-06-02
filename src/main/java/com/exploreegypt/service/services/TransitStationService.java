package com.exploreegypt.service.services;

import com.exploreegypt.entity.TransitStation;
import com.exploreegypt.repository.TransitStationRepository;
import org.springframework.stereotype.Service;

@Service
public class TransitStationService extends AbstractCurdService<TransitStationRepository, TransitStation> {

    public TransitStationService(TransitStationRepository repo) {
        super(repo);
    }
}
