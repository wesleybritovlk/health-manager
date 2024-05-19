package com.github.wesleybritovlk.healthmanager.common;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonService<Model, Request, Response> {
    Map<Object, Object> create(Request request);

    Response findById(UUID id);

    Page<Response> findAll(Pageable pageable);

    Map<Object, Object> update(UUID id, Request request);

    Map<Object, Object> delete(UUID id);
}
