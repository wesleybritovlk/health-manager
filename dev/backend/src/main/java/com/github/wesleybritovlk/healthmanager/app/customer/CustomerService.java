package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.common.CommonService;

import lombok.RequiredArgsConstructor;

public interface CustomerService extends CommonService<Customer, Request, Response> {
}

@Service
@Transactional
@RequiredArgsConstructor
class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    private Customer findCustomer(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found, please check the id"));
    }

    @Override
    public Map<Object, Object> create(Request request) {
        Customer model = mapper.toModel(request);
        Customer created = repository.saveAndFlush(model);
        return mapper.toResponse(created.getId(), created.getName());
    }

    @Override
    public Response findById(UUID id) {
        Customer customer = findCustomer(id);
        BigInteger severitySumById = repository.findSeveritySumById(id);
        return mapper.toResponse(customer, severitySumById);
    }

    @Override
    public Page<Response> findAll(Pageable pageable) {
        List<Response> responses = repository.findAll().stream()
                .map(customer -> mapper.toResponse(customer, repository.findSeveritySumById(customer.getId())))
                .sorted((res0, res1) -> res1.score().compareTo(res0.score())).toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());
        return new PageImpl<>(start > end ? List.of() : responses.subList(start, end), pageable, responses.size());
    }

    @Override
    public Map<Object, Object> update(UUID id, Request request) {
        Customer customer = findCustomer(id);
        Customer model = mapper.toModel(customer, request);
        String name = repository.saveAndFlush(model).getName();
        return mapper.toResponse(id, name);
    }

    @Override
    public Map<Object, Object> delete(UUID id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Customer not found, please check the id");
        repository.deleteById(id);
        return mapper.toResponse(id);
    }
}
