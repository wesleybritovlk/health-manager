package com.github.wesleybritovlk.healthmanager.app.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblem;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemMapper;;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {
        private CustomerMapper mapper;
        @Mock
        private HealthProblemMapper healthProblemMapper;

        private Request request;
        private Customer customer;

        private HealthProblemDTO.Response responseProblem;
        private HealthProblemDTO.Response responseProblem1;
        private HealthProblemDTO.Response responseProblem2;
        private Customer customerFull;

        @BeforeEach
        void setup() {
                mapper = new CustomerMapperImpl(healthProblemMapper);
                request = new Request("foo", LocalDate.parse("1998-07-23"), Sex.MALE);
                customer = Customer.builder().id(UUID.randomUUID()).name("fooResponse")
                                .dateBirth(LocalDate.parse("1998-06-20"))
                                .sex(Sex.NOT_KNOW).healthProblems(new TreeSet<>()).build();

                UUID customerId = UUID.randomUUID();
                HealthProblem problem = HealthProblem.builder().id(UUID.randomUUID())
                                .customer(Customer.builder().id(customerId).build()).severity(BigInteger.ONE).build();
                HealthProblem problem1 = HealthProblem.builder().id(UUID.randomUUID())
                                .customer(Customer.builder().id(customerId).build()).severity(BigInteger.TWO).build();
                HealthProblem problem2 = HealthProblem.builder().id(UUID.randomUUID())
                                .customer(Customer.builder().id(customerId).build()).severity(BigInteger.ONE).build();
                responseProblem = new HealthProblemDTO.Response(problem.getId(), customerId, "test1",
                                BigInteger.ONE);
                responseProblem1 = new HealthProblemDTO.Response(problem1.getId(), customerId,
                                "test2", BigInteger.TWO);
                responseProblem2 = new HealthProblemDTO.Response(problem2.getId(), customerId,
                                "test3", BigInteger.ONE);
                customerFull = Customer.builder().id(customerId).name("fooFull")
                                .dateBirth(LocalDate.parse("2001-11-09"))
                                .sex(Sex.FEMALE).healthProblems(Set.of(problem, problem1, problem2)).build();
        }

        @Test
        @SuppressWarnings("deprecation")
        void itShouldReturnThrownUnimplementedMetod_toResponseDeprecated() {
                assertThatThrownBy(() -> mapper.toResponse(customer))
                                .isInstanceOf(UnsupportedOperationException.class)
                                .hasMessageContaining("Unimplemented method 'toResponse'");
        }

        @Test
        void itShouldMapCustomerModel_WithCustomerRequest() {
                Customer model = mapper.toModel(request);
                assertThat(model).isNotNull();
                assertThat(model.getName()).isEqualTo("foo");
                assertThat(model.getDateBirth()).isEqualTo(LocalDate.parse("1998-07-23"));
                assertThat(model.getSex()).isEqualTo(Sex.MALE);
                assertThat(model.getHealthProblems()).isNotNull();
                assertThat(model.getHealthProblems()).isEmpty();
        }

        @Test
        void itShouldMapCustomerModel_WithCustomerAndCustomerRequest() {
                Customer model = mapper.toModel(customer, request);
                assertThat(model).isNotNull();
                assertThat(model.getName()).isEqualTo("foo");
                assertThat(model.getDateBirth()).isEqualTo(LocalDate.parse("1998-07-23"));
                assertThat(model.getSex()).isEqualTo(Sex.MALE);
                assertThat(model.getHealthProblems()).isNotNull();
                assertThat(model.getHealthProblems()).isEmpty();
        }

        @Test
        void itShouldMapCustomerResponse_WithOnlyCustomer() {
                Response response = mapper.toResponse(customer, BigInteger.valueOf(4));
                assertThat(response).isNotNull();
                assertThat(response.id()).isEqualTo(customer.getId());
                assertThat(response.full_name()).isEqualTo("fooResponse");
                assertThat(response.date_birth()).isEqualTo(LocalDate.parse("1998-06-20"));
                assertThat(response.sex()).isEqualTo(Sex.NOT_KNOW);
                assertThat(response.score()).isEqualTo(BigDecimal.valueOf(76.86));
                assertThat(response.health_problems()).isNotNull();
                assertThat(response.health_problems()).isEmpty();
        }

        @Test       
        void itShouldMapCustomerResponse_WithSeveritySumZero() {
                Customer customer0 = Customer.builder().name("zero").dateBirth(LocalDate.parse("1998-07-23")).sex(Sex.NOT_KNOW).healthProblems(new TreeSet<>()).build();
                Response response = mapper.toResponse(customer0, BigInteger.ZERO);
                assertThat(response).isNotNull();
                assertThat(response.score()).isEqualTo(BigDecimal.ZERO.setScale(2));
        }

        @Test
        void itShouldMapCustomerResponse_WithCustomerAndHealthProblems() {
                when(healthProblemMapper.toResponse(any(HealthProblem.class))).thenReturn(responseProblem,
                                responseProblem1,
                                responseProblem2);
                Response response = mapper.toResponse(customerFull, BigInteger.valueOf(4));
                verify(healthProblemMapper, times(3)).toResponse(any(HealthProblem.class));
                assertThat(response).isNotNull();
                assertThat(response.id()).isEqualTo(customerFull.getId());
                assertThat(response.full_name()).isEqualTo("fooFull");
                assertThat(response.date_birth()).isEqualTo(LocalDate.parse("2001-11-09"));
                assertThat(response.sex()).isEqualTo(Sex.FEMALE);
                assertThat(response.score()).isEqualTo(BigDecimal.valueOf(76.86));
                assertThat(response.health_problems()).isNotEmpty();
                assertThat(response.health_problems()).hasSize(3);
        }

        @Test
        void itShouldMapCustomerResponse_WithCustomerIdAndCustomerName() {
                assertThat(mapper.toResponse(UUID.randomUUID(), "foo")).containsKeys("id", "full_name");
                assertThat(mapper.toResponse(UUID.randomUUID())).containsKeys("id");
        }
}
