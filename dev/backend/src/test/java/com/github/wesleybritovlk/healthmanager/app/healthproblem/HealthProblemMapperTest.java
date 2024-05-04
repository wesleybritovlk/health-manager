package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;

class HealthProblemMapperTest {
    private HealthProblemMapper mapper;

    private Request request;
    private Customer customer;
    private HealthProblem healthProblem;

    @BeforeEach
    void setup() {
        mapper = new HealthProblemMapperImpl();
        customer = Customer.builder().id(UUID.randomUUID()).build();
        request = new Request(customer.getId(), "test1", BigInteger.ONE);
        healthProblem = HealthProblem.builder().id(UUID.randomUUID()).customer(customer)
                .problemName("test").severity(BigInteger.TWO).build();
    }

    @Test
    @SuppressWarnings("deprecation")
    void itShouldReturnThrownUnimplementedMetod_toModelDeprecated() {
        assertThatThrownBy(() -> mapper.toModel(request))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Unimplemented method 'toModel'");
    }

    @Test
    void itShouldMapHealthProblemModel_withHealthProblemRequestAndCustomer() {
        HealthProblem model = mapper.toModel(request, customer);
        assertThat(model).isNotNull();
        assertThat(model.getCustomer()).isEqualTo(customer);
        assertThat(model.getProblemName()).isEqualTo("test1");
        assertThat(model.getSeverity()).isEqualTo(BigInteger.ONE);
    }

    @Test
    void itShouldMapHealthProblemModelToUpdate_withHealthProblemAndHealthProblemRequest() {
        HealthProblem model = mapper.toModel(healthProblem, request);
        assertThat(model).isNotNull();
        assertThat(model.getCustomer()).isEqualTo(customer);
        assertThat(model.getProblemName()).isEqualTo("test1");
        assertThat(model.getSeverity()).isEqualTo(BigInteger.ONE);
    }

    @Test
    void itShouldMapHealthProblemResponse_WithHealthProblem() {
        Response response = mapper.toResponse(healthProblem);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(healthProblem.getId());
        assertThat(response.customer_id()).isEqualTo(healthProblem.getCustomer().getId());
        assertThat(response.problem_name()).isEqualTo("test");
        assertThat(response.severity()).isEqualTo(BigInteger.TWO);
    }
}
