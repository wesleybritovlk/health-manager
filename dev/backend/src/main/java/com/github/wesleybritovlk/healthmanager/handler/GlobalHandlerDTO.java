package com.github.wesleybritovlk.healthmanager.handler;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GlobalHandlerResponse", title = "GlobalHandlerResponse")
public record GlobalHandlerDTO(
                ZonedDateTime timestamp,
                int status,
                String error,
                String message,
                String request_path) {
}
