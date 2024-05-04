package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigInteger;
import java.util.UUID;

import org.springframework.data.domain.Page;
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
    public Customer create(Request request) {
        Customer model = mapper.toModel(request);
        return repository.saveAndFlush(model);
    }

    @Override
    public Response findById(UUID id) {
        Customer customer = findCustomer(id);
        BigInteger severitySumById = repository.findSeveritySumById(id);
        return mapper.toResponse(customer, severitySumById);
    }

    @Override
    public Page<Response> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(customer -> {
                    BigInteger severitySumById = repository.findSeveritySumById(customer.getId());
                    return mapper.toResponse(customer, severitySumById);
                });
    }

    @Override
    public Customer update(UUID id, Request request) {
        Customer customer = findCustomer(id);
        Customer model = mapper.toModel(customer, request);
        return repository.saveAndFlush(model);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Customer not found, please check the id");
        repository.deleteById(id);
    }
}
