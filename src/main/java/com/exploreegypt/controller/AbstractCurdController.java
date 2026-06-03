package com.exploreegypt.controller;

import com.exploreegypt.entity.BaseService;
import com.exploreegypt.service.services.AbstractCurdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCurdController<S extends AbstractCurdService<?, E>, E extends BaseService, Req, Res> {
    protected final S service;

    protected AbstractCurdController(S service) {
        this.service = service;
    }

    protected abstract E toEntity(Req request);
    protected abstract Res toResponse(E entity);

    public ResponseEntity<Iterable<Res>> getAll() {
        List<Res> responses = new ArrayList<>();
        service.findAll().forEach(entity -> responses.add(toResponse(entity)));
        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<Res> getById(UUID id) {
        E entity = service.findById(id);
        return entity != null ? ResponseEntity.ok(toResponse(entity)) : ResponseEntity.notFound().build();
    }

    public ResponseEntity<Res> create(Req request) {
        E entity = toEntity(request);
        E saved = service.save(entity);
        return ResponseEntity.ok(toResponse(saved));
    }

    public ResponseEntity<Res> update(UUID id, Req request) {
        E existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        E entity = toEntity(request);
        entity.setId(id);
        E updated = service.save(entity);
        return ResponseEntity.ok(toResponse(updated));
    }

    public ResponseEntity<Void> delete(UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
