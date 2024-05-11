package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static java.util.Comparator.comparing;
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
        customerEmpty = Customer.builder().id(UUID.randomUUID()).name("foo")
                .dateBirth(LocalDate.parse("1999-12-01")).sex(Sex.MALE).healthProblems(new TreeSet<>()).build();
        healthProblemCreate = HealthProblem.builder().id(UUID.randomUUID()).customer(customerEmpty).hpName("test")
                .severity(BigInteger.ONE).build();
        requestCreate = new Request(customerEmpty.getId(), "test", BigInteger.ONE);

        Set<HealthProblem> healthProblems = new TreeSet<>(comparing(HealthProblem::getHpName));
        customer = Customer.builder().id(UUID.randomUUID()).name("foo1")
                .dateBirth(LocalDate.parse("2000-01-20")).sex(Sex.FEMALE).healthProblems(healthProblems).build();
        healthProblemUpdate = HealthProblem.builder().id(UUID.randomUUID()).customer(customer).hpName("test1")
                .severity(BigInteger.TWO).build();
        healthProblems.add(healthProblemUpdate);
        response = new Response(healthProblemUpdate.getId(), customer.getId(), "test1", BigInteger.TWO);
    }

    @Test
    void itShouldCreateHealthProblem_ByHealthProblemDTORequest() {
        when(customerRepo.findById(any(UUID.class))).thenReturn(Optional.of(customerEmpty));
        when(mapper.toModel(any(Request.class), any(Customer.class))).thenReturn(healthProblemCreate);
        when(repository.saveAndFlush(any(HealthProblem.class))).thenReturn(healthProblemCreate);

        service.create(requestCreate);

        verify(customerRepo, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toModel(any(Request.class), any(Customer.class));
        verify(repository, times(1)).saveAndFlush(any(HealthProblem.class));
    }

    @Test
    void itShouldReturnHealthProblemResponse_byHealthProblemId() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));
        when(mapper.toResponse(any(HealthProblem.class))).thenReturn(response);

        service.findById(healthProblemUpdate.getId());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toResponse(any(HealthProblem.class));
        assertThatThrownBy(() -> service.findById(any(UUID.class))).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Health Problem not found, please check the id");
    }

    @Test
    void itShouldReturnAllHealthProblemsResponseInPage_byPageRequest() {
        List<HealthProblem> healthProblems = List.of(healthProblemUpdate);
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll()).thenReturn(healthProblems);
        when(mapper.toResponse(any(HealthProblem.class))).thenReturn(response);

        service.findAll(pageable);

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toResponse(any(HealthProblem.class));
    }

    @Test
    void itShouldReturnAllHealthProblemsResponseInPage_ifPageIsEmpty() {
        Pageable pageable = PageRequest.of(1, 10);
        when(repository.findAll()).thenReturn(List.of());

        service.findAll(pageable);

        verify(repository, times(1)).findAll();
    }

    @Test
    void itShouldUpdateHealthProblem_ByHealthProblemDTORequest() {
        UUID id = healthProblemUpdate.getId();
        HealthProblem healthProblem = HealthProblem.builder().id(id).customer(customer)
                .hpName("testUpdated").severity(BigInteger.ONE).build();
        Request requestUpdate = new Request(customer.getId(), "testUpdated", BigInteger.ONE);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));
        when(mapper.toModel(any(HealthProblem.class), any(Request.class))).thenReturn(healthProblem);
        when(repository.saveAndFlush(any(HealthProblem.class))).thenReturn(healthProblem);

        service.update(id, requestUpdate);

        verify(repository, times(1)).findById(any(UUID.class));
        verify(mapper, times(1)).toModel(any(HealthProblem.class), any(Request.class));
        verify(repository, times(1)).saveAndFlush(any(HealthProblem.class));
    }

    @Test
    void itShouldThrowConflict_ifTryUpdateHealthProblemWithAlreadyExistingName() {
        UUID id = UUID.randomUUID();
        HealthProblem hp = HealthProblem.builder().id(id).customer(customer).hpName("problem2").severity(BigInteger.TWO)
                .build();
        Request conflictRequest = new Request(id, "problem1", BigInteger.TWO);
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(hp));
        when(repository.existsByCustomerIdAndHpName(any(UUID.class), any(String.class))).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, conflictRequest)).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("This Health Problem already exists in this Customer");

    }

    @Test
    void itShouldDeleteHealthProblemById() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(healthProblemUpdate));

        service.delete(healthProblemUpdate.getId());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).delete(any(HealthProblem.class));
    }
}
