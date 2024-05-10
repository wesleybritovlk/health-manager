package com.github.wesleybritovlk.healthmanager.handler;

import java.time.ZonedDateTime;

public record GlobalHandlerDTO(
                ZonedDateTime timestamp,
                int status,
                String error,
                String message,
                String request_path) {
}
