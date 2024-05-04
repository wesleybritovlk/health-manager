package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import org.springframework.stereotype.Component;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;
import com.github.wesleybritovlk.healthmanager.common.CommonMapper;

import lombok.RequiredArgsConstructor;

public interface HealthProblemMapper
        extends CommonMapper<HealthProblem, HealthProblemDTO.Request, HealthProblemDTO.Response> {
    public HealthProblem toModel(Request request, Customer customer);

    @Override
    @Deprecated
    /**
     * Unimplemented method, use
     * {@link HealthProblemMapper#toModel(Request, Customer)}
     * 
     * @deprecated
     */
    default HealthProblem toModel(Request request) {
        throw new UnsupportedOperationException("Unimplemented method 'toModel'");
    }
}

@Component
@RequiredArgsConstructor
class HealthProblemMapperImpl implements HealthProblemMapper {

    @Override
    public HealthProblem toModel(Request request, Customer customer) {
        return HealthProblem.builder().customer(customer).problemName(request.problem_name())
                .severity(request.severity()).build();
    }

    @Override
    public HealthProblem toModel(HealthProblem model, Request request) {
        return HealthProblem.builder().customer(model.getCustomer()).problemName(request.problem_name())
                .severity(request.severity()).build();
    }

    @Override
    public Response toResponse(HealthProblem model) {
        return new Response(model.getId(), model.getCustomer().getId(), model.getProblemName(), model.getSeverity());
    }
}
