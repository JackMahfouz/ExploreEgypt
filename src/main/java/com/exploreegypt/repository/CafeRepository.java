package com.exploreegypt.repository;

import com.exploreegypt.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, UUID> {
}
