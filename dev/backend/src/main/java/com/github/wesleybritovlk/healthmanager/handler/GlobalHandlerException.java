package com.github.wesleybritovlk.healthmanager.handler;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

public interface GlobalHandlerException {
        ResponseEntity<GlobalHandlerDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                        HttpServletRequest request);

        ResponseEntity<GlobalHandlerDTO> handleResponseStatusException(ResponseStatusException ex,
                        HttpServletRequest request);

        ResponseEntity<GlobalHandlerDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                        HttpServletRequest request);

        ResponseEntity<Object> handleUnexpectedException(Throwable ex, HttpServletRequest request);

        ResponseEntity<GlobalHandlerDTO> handlePropertyReferenceException(PropertyReferenceException ex,
                        HttpServletRequest request);
}

@RestControllerAdvice
@RequiredArgsConstructor
class GlobalHandlerExceptionImpl implements GlobalHandlerException {
        private final GlobalHandlerService service;

        @Override
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<GlobalHandlerDTO> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
                String message = allErrors.stream().map(ObjectError::getDefaultMessage)
                                .collect(Collectors.joining(", "));
                GlobalHandlerDTO dto = new GlobalHandlerDTO(ZonedDateTime.now(), status.value(),
                                status.getReasonPhrase(), message, request.getRequestURI());
                service.create(dto);
                return ResponseEntity.status(status).body(dto);
        }

        @Override
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<GlobalHandlerDTO> handleResponseStatusException(ResponseStatusException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                GlobalHandlerDTO dto = new GlobalHandlerDTO(ZonedDateTime.now(), status.value(),
                                status.getReasonPhrase(), ex.getReason(), request.getRequestURI());
                service.create(dto);
                return ResponseEntity.status(status).body(dto);
        }

        @Override
        @ExceptionHandler(HttpMessageNotReadableException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<GlobalHandlerDTO> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                GlobalHandlerDTO dto = new GlobalHandlerDTO(ZonedDateTime.now(), status.value(),
                                status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
                service.create(dto);
                return ResponseEntity.status(status).body(dto);
        }

        @Override
        @ExceptionHandler(Throwable.class)
        public ResponseEntity<Object> handleUnexpectedException(Throwable ex, HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                GlobalHandlerDTO dto = new GlobalHandlerDTO(ZonedDateTime.now(), status.value(),
                                status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
                service.create(dto);
                return ResponseEntity.status(status).body(dto);
        }

        @Override
        @ExceptionHandler(PropertyReferenceException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<GlobalHandlerDTO> handlePropertyReferenceException(PropertyReferenceException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                GlobalHandlerDTO dto = new GlobalHandlerDTO(ZonedDateTime.now(), status.value(),
                                status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
                service.create(dto);
                return ResponseEntity.status(status).body(dto);
        }
}
