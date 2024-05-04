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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerDTO.Response;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblem;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
        private CustomerService service;
        @Mock
        private CustomerRepository repository;
        @Mock
        private CustomerMapper mapper;

        private Customer customerCreate;
        private Request requestCreate;
        private Customer customerUpdate;
        private Response response;

        @BeforeEach
        void setup() {
                service = new CustomerServiceImpl(repository, mapper);
                customerCreate = Customer.builder().id(UUID.randomUUID()).fullName("foo")
                                .dateBirth(LocalDate.parse("1999-12-01"))
                                .sex(Sex.MALE).healthProblems(new TreeSet<>()).build();
                requestCreate = new Request("foo", LocalDate.parse("1999-12-01"), Sex.MALE);

                customerUpdate = Customer.builder().id(UUID.randomUUID()).fullName("foo1")
                                .dateBirth(LocalDate.parse("2000-01-20")).sex(Sex.FEMALE)
                                .healthProblems(Set.of(
                                                HealthProblem.builder().id(UUID.randomUUID()).customer(customerUpdate)
                                                                .problemName("test1").severity(BigInteger.ONE).build()))
                                .build();
                UUID customerId = customerUpdate.getId();
                response = new Response(customerUpdate.getId(), "foo1", LocalDate.parse("2000-01-20"), Sex.FEMALE,
                                BigDecimal.valueOf(2d),
                                Set.of(new HealthProblemDTO.Response(customerId, customerUpdate.getId(), "test1",
                                                BigInteger.ONE)));
        }

        @Test
        void itShouldCreateCustomer_WithCustomerRequest() {
                when(mapper.toModel(any(Request.class))).thenReturn(customerCreate);
                when(repository.saveAndFlush(any(Customer.class))).thenReturn(customerCreate);

                Customer created = service.create(requestCreate);

                verify(mapper, times(1)).toModel(any(Request.class));
                verify(repository, times(1)).saveAndFlush(any(Customer.class));
                assertThat(created).isNotNull();
        }

        @Test
        void itShouldReturnCustomerResponse_WithCustomerById() {
                when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customerUpdate));
                when(repository.findSeveritySumById(any(UUID.class))).thenReturn(BigInteger.valueOf(4));
                when(mapper.toResponse(any(Customer.class), any(BigInteger.class))).thenReturn(response);

                Response returnedById = service.findById(customerUpdate.getId());

                verify(repository, times(1)).findById(any(UUID.class));
                verify(repository, times(1)).findSeveritySumById(any(UUID.class));
                verify(mapper, times(1)).toResponse(any(Customer.class), any(BigInteger.class));
                assertThat(returnedById).isNotNull();
                assertThatThrownBy(() -> service.findById(any(UUID.class))).isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Customer not found, please check the id");
        }

        @Test
        void itShouldReturnAllCustomersResponseInPage_byPageRequest() {
                List<Customer> customers = List.of(customerUpdate);
                Page<Customer> customerPages = new PageImpl<>(customers);
                Pageable pageable = PageRequest.of(0, 10);
                when(repository.findAll(any(Pageable.class))).thenReturn(customerPages);
                when(repository.findSeveritySumById(any(UUID.class))).thenReturn(BigInteger.valueOf(4));
                when(mapper.toResponse(any(Customer.class), any(BigInteger.class))).thenReturn(response);

                Page<Response> returnAll = service.findAll(pageable);

                verify(repository, times(1)).findAll(any(Pageable.class));
                verify(repository, times(1)).findSeveritySumById(any(UUID.class));
                verify(mapper, times(1)).toResponse(any(Customer.class), any(BigInteger.class));
                assertThat(returnAll.getTotalElements()).isEqualTo(customers.size());
                assertThat(returnAll.getTotalPages()).isEqualTo(1);
                assertThat(returnAll.getContent()).isNotEmpty();
                assertThat(returnAll.getContent().get(0)).isNotNull();
        }

        @Test
        void itShouldUpdateCustomer_ByCustomerDTORequest() {
                UUID id = customerUpdate.getId();
                Customer customer = Customer.builder().id(id).fullName("fooUpdate")
                                .dateBirth(LocalDate.parse("2000-01-21")).sex(Sex.NOT_APPLICABLE)
                                .healthProblems(customerUpdate.getHealthProblems()).build();
                Request requestUpdate = new Request("fooUpdate", LocalDate.parse("2000-01-21"), Sex.NOT_APPLICABLE);
                when(repository.findById(any(UUID.class))).thenReturn(Optional.of(customerUpdate));
                when(mapper.toModel(any(Customer.class), any(Request.class))).thenReturn(customer);
                when(repository.saveAndFlush(any(Customer.class))).thenReturn(customer);

                Customer updated = service.update(id, requestUpdate);

                verify(repository, times(1)).findById(any(UUID.class));
                verify(mapper, times(1)).toModel(any(Customer.class), any(Request.class));
                verify(repository, times(1)).saveAndFlush(any(Customer.class));
                assertThat(updated).isNotNull();
                assertThat(updated.getId()).isEqualTo(id);
                assertThat(updated.getFullName()).isEqualTo("fooUpdate");
                assertThat(updated.getDateBirth()).isEqualTo(LocalDate.parse("2000-01-21"));
                assertThat(updated.getSex()).isEqualTo(Sex.NOT_APPLICABLE);
        }

        @Test
        void itShouldDeleteCustomerById() {
                when(repository.existsById(any(UUID.class))).thenReturn(true);
                service.delete(customerUpdate.getId());
                verify(repository, times(1)).existsById(any(UUID.class));
                verify(repository, times(1)).deleteById(any(UUID.class));
                assertThatThrownBy(() -> service.delete(any(UUID.class))).isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Customer not found, please check the id");
        }
}
