package com.github.wesleybritovlk.healthmanager.common;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonService<Model, Request, Response> {
    Model create(Request request);

    Response findById(UUID id);

    Page<Response> findAll(Pageable pageable);

    Model update(UUID id, Request request);

    void delete(UUID id);
}
