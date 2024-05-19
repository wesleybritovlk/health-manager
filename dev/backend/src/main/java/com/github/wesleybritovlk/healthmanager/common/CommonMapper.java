package com.github.wesleybritovlk.healthmanager.common;

import java.util.Map;

public interface CommonMapper<Model, Request, Response> {
    Model toModel(Request request);

    Model toModel(Model model, Request request);

    Response toResponse(Model model);

    Map<Object, Object> toResponse(Object... args);
}
