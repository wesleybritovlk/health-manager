package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer;
import com.github.wesleybritovlk.healthmanager.app.customer.CustomerRepository;

@DataJpaTest
class HealthProblemRepositoryTest {
    private final HealthProblemRepository repository;
    private final CustomerRepository customerRepo;

    @Autowired
    public HealthProblemRepositoryTest(HealthProblemRepository repository, CustomerRepository customerRepo) {
        this.repository = repository;
        this.customerRepo = customerRepo;
    }

    @BeforeEach
    void setup() {
        Set<HealthProblem> healthProblems = new TreeSet<>(comparing(HealthProblem::getProblemName));
        Customer customer = Customer.builder().fullName("foo")
                .dateBirth(LocalDate.parse("1999-12-01")).sex(Customer.Sex.MALE).healthProblems(healthProblems)
                .build();

        healthProblems.add(
                HealthProblem.builder().customer(customer).problemName("test").severity(BigInteger.ONE).build());
        healthProblems.add(
                HealthProblem.builder().customer(customer).problemName("test1").severity(BigInteger.TWO).build());

        customerRepo.saveAndFlush(customer);
        repository.saveAllAndFlush(healthProblems);
    }

    @Test
    void itShouldUpdateHealthProblem_andBugWarning() {
        HealthProblem healthProblem = repository.findAll().get(1);
        UUID healthProblemId = healthProblem.getId();
        ZonedDateTime oldUpdatedAt = healthProblem.getUpdatedAt();
        Customer oldCustomer = healthProblem.getCustomer();
        int oldHPHash = healthProblem.hashCode();

        HealthProblem update = HealthProblem.builder().id(healthProblemId).customer(oldCustomer)
                .problemName("test1Updated").severity(BigInteger.ONE).build();

        repository.saveAndFlush(update);

        Optional<HealthProblem> updatedHP = repository.findById(healthProblemId);
        assertThat(updatedHP).isNotEmpty();
        assertThat(updatedHP.get().getUpdatedAt()).isNotEqualTo(oldUpdatedAt);
        assertThat(updatedHP.get().getCustomer()).isNotNull();
        assertThat(updatedHP.get().getCustomer()).isEqualTo(oldCustomer);
        assertThat(updatedHP.get().hashCode()).isNotEqualTo(oldHPHash);
    }

    @Test
    void itShouldDeleteFirstHealthProblem_AndRemoveHealthProblemFromCustomerList() {
        List<HealthProblem> hProblems = repository.findAll();
        assertThat(hProblems).hasSize(2);

        HealthProblem oldHP = hProblems.get(0);
        UUID id = oldHP.getId();
        Customer customerByHP = oldHP.getCustomer();
        customerByHP.getHealthProblems().remove(oldHP);
        repository.delete(oldHP);

        Customer customer = customerRepo.findById(customerByHP.getId()).get();
        assertThat(repository.findById(id)).isEmpty();
        assertThat(customer.getHealthProblems()).hasSize(1);
    }
}
