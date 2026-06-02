package com.exploreegypt.service.services;

import com.exploreegypt.entity.Site;
import com.exploreegypt.repository.SiteRepository;
import org.springframework.stereotype.Service;

@Service
public class SiteService extends AbstractCurdService<SiteRepository, Site> {

    public SiteService(SiteRepository repo) {
        super(repo);
    }
}
