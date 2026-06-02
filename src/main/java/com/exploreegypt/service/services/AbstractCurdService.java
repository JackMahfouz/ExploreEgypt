package com.exploreegypt.service.services;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public abstract class AbstractCurdService<R extends JpaRepository<T, UUID>, T> {
    protected final R repo;

    protected AbstractCurdService(R repo) {
        this.repo = repo;
    }
    
    public Iterable<T> findAll() {
        return repo.findAll();
    }
    
    public T findById(UUID id) {
        return repo.findById(id).orElse(null);
    }
    
    public T save(T entity) {
        return repo.save(entity);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
