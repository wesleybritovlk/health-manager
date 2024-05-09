package com.github.wesleybritovlk.healthmanager.common;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface CommonController<Request, Response> {
    ResponseEntity<Map<Object, Object>> create(Request request);

    ResponseEntity<Map<Object, Object>> getById(UUID id);

    ResponseEntity<Page<Response>> getAll(Pageable pageable);

    ResponseEntity<Map<Object, Object>> update(UUID id, Request request);

    ResponseEntity<Map<Object, Object>> delete(UUID id);
}
