package com.github.wesleybritovlk.healthmanager.app.customer;

import static java.util.Comparator.comparing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblem;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemMapper;
import com.github.wesleybritovlk.healthmanager.common.CommonMapper;

import lombok.RequiredArgsConstructor;

public interface CustomerMapper extends CommonMapper<Customer, Request, Response> {
    Response toResponse(Customer model, BigInteger severitySum);

    @Override
    @Deprecated
    /**
     * Unimplemented method, use
     * {@link CustomerMapper#toResponse(Customer)}
     * 
     * @deprecated
     */
    default Response toResponse(Customer model) {
        throw new UnsupportedOperationException("Unimplemented method 'toResponse'");
    }
}

@Component
@RequiredArgsConstructor
class CustomerMapperImpl implements CustomerMapper {
    private final HealthProblemMapper healthProblemMapper;

    @Override
    public Customer toModel(Request request) {
        return Customer.builder().fullName(request.full_name()).dateBirth(request.date_birth()).sex(request.sex())
                .healthProblems(new TreeSet<>(comparing(HealthProblem::getProblemName))).build();
    }

    @Override
    public Customer toModel(Customer model, Request request) {
        return Customer.builder().id(model.getId()).fullName(request.full_name()).dateBirth(request.date_birth())
                .sex(request.sex()).healthProblems(model.getHealthProblems()).build();
    }

    private BigDecimal getSeverityScore(BigInteger severitySum, RoundingMode roundingMode) {
        double exp = -2.8 + severitySum.intValue(), euler = Math.E, pow = Math.pow(euler, -exp);
        BigDecimal scalePow = BigDecimal.valueOf(pow).setScale(6, roundingMode), one = BigDecimal.ONE,
                sum = one.add(scalePow), hundred = BigDecimal.valueOf(100), divide = one.divide(sum, 4, roundingMode);
        return hundred.multiply(divide).setScale(2, roundingMode);
    }

    private Set<HealthProblemDTO.Response> getHealthProblemsToResponse(Set<HealthProblem> healthProblems) {
        return !healthProblems.isEmpty()
                ? healthProblems.stream().map(healthProblemMapper::toResponse).collect(Collectors.toSet())
                : new TreeSet<>();
    }

    @Override
    public Response toResponse(Customer model, BigInteger severitySum) {
        return new Response(model.getId(), model.getFullName(), model.getDateBirth(), model.getSex(),
                getSeverityScore(severitySum, RoundingMode.UP), getHealthProblemsToResponse(model.getHealthProblems()));
    }
}
