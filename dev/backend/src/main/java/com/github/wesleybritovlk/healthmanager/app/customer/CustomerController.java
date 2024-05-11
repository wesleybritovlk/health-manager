package com.github.wesleybritovlk.healthmanager.app.customer;

import static com.github.wesleybritovlk.healthmanager.common.CommonResource.toResource;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.common.CommonController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

public interface CustomerController extends CommonController<Request, Response> {
}

@RestController
@RequestMapping("api/customers")
@RequiredArgsConstructor
class CustomerControllerImpl implements CustomerController {
    private final CustomerService service;

    @Override
    @PostMapping
    public ResponseEntity<Map<Object, Object>> create(@Valid @RequestBody Request request) {
        var response = service.create(request);
        var resource = toResource("Customer created successfully!", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity<Map<Object, Object>> getById(@PathVariable UUID id) {
        var response = service.findById(id);
        return ResponseEntity.ok(toResource(response));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<Response>> getAll(
            @RequestParam(name = "page", required = false) Integer pageNumber,
            @RequestParam(name = "size", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize != null ? pageSize : 10);
        Page<Response> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<Map<Object, Object>> update(@PathVariable UUID id, @Valid @RequestBody Request request) {
        var response = service.update(id, request);
        var resource = toResource("Customer updated successfully!", response);
        return ResponseEntity.ok(resource);
    }

    @Override
    @DeleteMapping("{id}")
    public ResponseEntity<Map<Object, Object>> delete(@PathVariable UUID id) {
        var response = service.delete(id);
        var resource = toResource("Customer deleted successfully!", response);
        return ResponseEntity.ok(resource);
    }
}
