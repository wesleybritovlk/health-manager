package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerRepository;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;
import com.github.wesleybritovlk.healthmanager.common.CommonService;

import lombok.RequiredArgsConstructor;

public interface HealthProblemService
        extends CommonService<HealthProblem, Request, Response> {
}

@Service
@Transactional
@RequiredArgsConstructor
class HealthProblemServiceImpl implements HealthProblemService {
    private final HealthProblemRepository repository;
    private final HealthProblemMapper mapper;
    private final CustomerRepository customerRepo;

    private Customer findCustomer(UUID customerId) {
        return customerRepo.findById(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found, please check the id"));
    }

    private HealthProblem findHealthProblem(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Health Problem not found, please check the id"));
    }

    @Override
    public HealthProblem create(Request request) {
        Customer customer = findCustomer(request.customer_id());
        HealthProblem model = mapper.toModel(request, customer);
        customerRepo.saveAndFlush(customer);
        return repository.saveAndFlush(model);
    }

    @Override
    public Response findById(UUID id) {
        HealthProblem healthProblem = findHealthProblem(id);
        return mapper.toResponse(healthProblem);
    }

    @Override
    public Page<Response> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public HealthProblem update(UUID id, Request request) {
        HealthProblem healthProblem = findHealthProblem(id);
        HealthProblem model = mapper.toModel(healthProblem, request);
        return repository.saveAndFlush(model);
    }

    @Override
    public void delete(UUID id) {
        HealthProblem healthProblem = findHealthProblem(id);
        healthProblem.getCustomer().getHealthProblems().remove(healthProblem);
        repository.delete(healthProblem);
    }
}
