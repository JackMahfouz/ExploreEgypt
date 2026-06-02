package com.exploreegypt.service.services;

import com.exploreegypt.entity.Cafe;
import com.exploreegypt.repository.CafeRepository;
import org.springframework.stereotype.Service;

@Service
public class CafeService extends AbstractCurdService<CafeRepository, Cafe> {

    public CafeService(CafeRepository repo) {
        super(repo);
    }
}
