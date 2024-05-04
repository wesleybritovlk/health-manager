package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.github.wesleybritovlk.healthmanager.app.customer.Customer;
import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerRepository;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Request;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO.Response;

@ExtendWith(MockitoExtension.class)
class HealthProblemServiceTest {
    private HealthProblemService service;
    @Mock
    private HealthProblemRepository repository;
    @Mock
    private HealthProblemMapper mapper;
    @Mock
    private CustomerRepository customerRepo;

    private Customer customerEmpty;
    private Customer customer;
    private HealthProblem healthProblemCreate;
    private Request requestCreate;
    private HealthProblem healthProblemUpdate;
    private Response response;

    @BeforeEach
    void setup() {
        service = new HealthProblemServiceImpl(repository, mapper, customerRepo);
        customerEmpty = Customer.builder().id(UUID.randomUUID()).fullName("foo")
                .dateBirth(LocalDate.parse("1999-12-01")).sex(Sex.MALE).healthProblems(new TreeSet<>()).build();
        healthProblemCreate = HealthProblem.builder().id(UUID.randomUUID()).customer(customerEmpty).problemName("test")
                .severity(BigInteger.ONE).build();
        requestCreate = new Request(customerEmpty.getId(), "test", BigInteger.ONE);

        Set<HealthProblem> healthProblems = new TreeSet<>(comparing(HealthProblem::getProblemName));
        customer = Customer.builder().id(UUID.randomUUID()).fullName("foo1")
                .dateBirth(LocalDate.parse("2000-01-20")).sex(Sex.FEMALE).healthProblems(healthProblems).build();
        healthProblemUpdate = HealthProblem.builder().id(UUID.randomUUID()).customer(customer).problemName("test1")
                .severity(BigInteger.TWO).build();
        healthProblems.add(healthProblemUpdate);
        response = new Response(healthProblemUpdate.getId(), customer.getId(), "test1", BigInteger.TWO);
    }

    @Test
    void itShouldCreateHealthProblem_ByHealthProblemDTORequest() {
        when(customerRepo.findById(any(UUID.class))).thenReturn(Optional.of(customerEmpty));
        when(mapper.toModel(any(Request.class), any(Customer.class))).thenReturn(healthProblemCreate);
        when(repository.saveAndFlush(any(HealthProblem.class))).thenReturn(healthProblemCreate);

        HealthProblem created = service.create(requestCreate);

        verify(customerRepo, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toModel(any(Request.class), any(Customer.class));
        verify(repository, times(1)).saveAndFlush(any(HealthProblem.class));
        assertThat(created).isNotNull();
    }

    @Test
    void itShouldReturnHealthProblemResponse_byHealthProblemId() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));
        when(mapper.toResponse(any(HealthProblem.class))).thenReturn(response);

        Response returnedById = service.findById(healthProblemUpdate.getId());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toResponse(any(HealthProblem.class));
        assertThat(returnedById).isNotNull();
        assertThatThrownBy(() -> service.findById(any(UUID.class))).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Health Problem not found, please check the id");
    }

    @Test
    void itShouldReturnAllHealthProblemsResponseInPage_byPageRequest() {
        List<HealthProblem> healthProblems = List.of(healthProblemUpdate);
        Page<HealthProblem> healthProblemPages = new PageImpl<>(healthProblems);
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Pageable.class))).thenReturn(healthProblemPages);
        when(mapper.toResponse(any(HealthProblem.class))).thenReturn(response);

        Page<Response> returnAll = service.findAll(pageable);

        verify(repository, times(1)).findAll(any(Pageable.class));
        verify(mapper, times(1)).toResponse(any(HealthProblem.class));
        assertThat(returnAll.getTotalElements()).isEqualTo(healthProblems.size());
        assertThat(returnAll.getTotalPages()).isEqualTo(1);
        assertThat(returnAll.getContent()).isNotEmpty();
        assertThat(returnAll.getContent().get(0)).isNotNull();
    }

    @Test
    void itShouldUpdateHealthProblem_ByHealthProblemDTORequest() {
        UUID id = healthProblemUpdate.getId();
        HealthProblem healthProblem = HealthProblem.builder().id(id).customer(customer)
                .problemName("testUpdated").severity(BigInteger.ONE).build();
        Request requestUpdate = new Request(customer.getId(), "testUpdated", BigInteger.ONE);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));
        when(mapper.toModel(any(HealthProblem.class), any(Request.class))).thenReturn(healthProblem);
        when(repository.saveAndFlush(any(HealthProblem.class))).thenReturn(healthProblem);

        HealthProblem updated = service.update(id, requestUpdate);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toModel(any(HealthProblem.class), any(Request.class));
        verify(repository, times(1)).saveAndFlush(any(HealthProblem.class));
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getProblemName()).isEqualTo("testUpdated");
        assertThat(updated.getSeverity()).isEqualTo(BigInteger.ONE);
    }

    @Test
    void itShouldDeleteHealthProblemById() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));

        service.delete(healthProblemUpdate.getId());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).delete(any(HealthProblem.class));
    }
}
