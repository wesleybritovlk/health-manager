package com.github.wesleybritovlk.healthmanager.common;

public interface CommonMapper<Model, Request, Response> {
    Model toModel(Request request);

    Model toModel(Model model, Request request);

    Response toResponse(Model model);
}
