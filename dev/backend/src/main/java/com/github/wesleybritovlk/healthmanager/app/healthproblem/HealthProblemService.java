package com.github.wesleybritovlk.healthmanager.app.healthproblem;

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

    private void checkHealthProblemsConflict(UUID customerId, String problemName) {
        if (repository.existsByCustomerIdAndHpName(customerId, problemName))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This Health Problem already exists in this Customer");
    }

    @Override
    public Map<Object, Object> create(Request request) {
        checkHealthProblemsConflict(request.customerId(), request.hpName());
        Customer customer = findCustomer(request.customerId());
        HealthProblem model = mapper.toModel(request, customer);
        customerRepo.saveAndFlush(customer);
        HealthProblem created = repository.saveAndFlush(model);
        return mapper.toResponse(created.getId(), created.getHpName());
    }

    @Override
    public Response findById(UUID id) {
        HealthProblem healthProblem = findHealthProblem(id);
        return mapper.toResponse(healthProblem);
    }

    @Override
    public Page<Response> findAll(Pageable pageable) {
        List<Response> responses = repository.findAll().stream().map(mapper::toResponse)
                .sorted((res0, res1) -> res1.severity().compareTo(res0.severity())).toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());
        return new PageImpl<>(start > end ? List.of() : responses.subList(start, end), pageable, responses.size());
    }

    @Override
    public Map<Object, Object> update(UUID id, Request request) {
        HealthProblem healthProblem = findHealthProblem(id);
        if (!healthProblem.getHpName().equals(request.hpName()))
            checkHealthProblemsConflict(request.customerId(), request.hpName());
        HealthProblem model = mapper.toModel(healthProblem, request);
        String hpName = repository.saveAndFlush(model).getHpName();
        return mapper.toResponse(id, hpName);
    }

    @Override
    public Map<Object, Object> delete(UUID id) {
        HealthProblem healthProblem = findHealthProblem(id);
        healthProblem.getCustomer().getHealthProblems().remove(healthProblem);
        repository.delete(healthProblem);
        return mapper.toResponse(id);
    }
}
