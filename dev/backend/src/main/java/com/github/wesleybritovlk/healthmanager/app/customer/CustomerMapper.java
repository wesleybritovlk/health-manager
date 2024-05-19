package com.github.wesleybritovlk.healthmanager.app.customer;

import static java.util.Comparator.comparing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        Set<HealthProblem> healthProblems = new TreeSet<>(comparing(HealthProblem::getId));
        return Customer.builder().name(request.name()).dateBirth(request.dateBirth()).sex(request.sex())
                .healthProblems(healthProblems).build();
    }

    @Override
    public Customer toModel(Customer model, Request request) {
        return Customer.builder().id(model.getId()).name(request.name()).dateBirth(request.dateBirth())
                .sex(request.sex()).healthProblems(model.getHealthProblems()).createdAt(model.getCreatedAt()).build();
    }

    private BigDecimal getSeverityScore(BigInteger severitySum, RoundingMode roundingMode) {
        if (severitySum.intValue() == 0)
            return BigDecimal.ZERO.setScale(2);
        double exp = -2.8 + severitySum.intValue(), euler = Math.E, pow = Math.pow(euler, -exp);
        BigDecimal scalePow = BigDecimal.valueOf(pow).setScale(6, roundingMode), one = BigDecimal.ONE,
                sum = one.add(scalePow), hundred = BigDecimal.valueOf(100), divide = one.divide(sum, 4, roundingMode);
        return hundred.multiply(divide).setScale(2, roundingMode);
    }

    private Set<HealthProblemDTO.Response> getHealthProblemsToResponse(Set<HealthProblem> healthProblems) {
        if (healthProblems.isEmpty())
            return Set.of();
        return healthProblems.stream()
                .sorted((hp0, hp1) -> Integer.compare(hp1.getSeverity().intValue(), hp0.getSeverity().intValue()))
                .map(healthProblemMapper::toResponse).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Response toResponse(Customer model, BigInteger severitySum) {
        return new Response(model.getId(), model.getName(), model.getDateBirth(), model.getSex(),
                getSeverityScore(severitySum, RoundingMode.UP), getHealthProblemsToResponse(model.getHealthProblems()));
    }

    @Override
    public Map<Object, Object> toResponse(Object... args) {
        var response = new TreeMap<>((key0, key1) -> ((String) key1).compareTo((String) key0));
        response.put("id", args[0]);
        if (args.length > 1)
            response.put("full_name", args[1]);
        return response;
    }
}
