package com.github.wesleybritovlk.healthmanager.app.customer;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblem;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemRepository;

@DataJpaTest
class CustomerRepositoryTest {
        private final CustomerRepository repository;
        private final HealthProblemRepository healthProblemRepo;

        @Autowired
        public CustomerRepositoryTest(CustomerRepository repository, HealthProblemRepository healthProblemRepo) {
                this.repository = repository;
                this.healthProblemRepo = healthProblemRepo;
        }

        @BeforeEach
        void setup() {
                Customer foo = Customer.builder().name("foO").dateBirth(LocalDate.parse("1999-12-01"))
                                .sex(Customer.Sex.MALE).healthProblems(Set.of()).build();
                Customer foo1 = Customer.builder().name("fOo1").dateBirth(LocalDate.parse("2000-01-20"))
                                .sex(Customer.Sex.NOT_APPLICABLE).healthProblems(Set.of()).build();

                Set<HealthProblem> healthProblems = new TreeSet<>(comparing(HealthProblem::getHpName));
                Customer join = Customer.builder().name("join").dateBirth(LocalDate.parse("1998-02-10"))
                                .sex(Customer.Sex.FEMALE).healthProblems(healthProblems).build();
                HealthProblem hpJoinFoo = HealthProblem.builder().customer(join).hpName("test")
                                .severity(BigInteger.ONE).build();
                HealthProblem hpJoin1Foo = HealthProblem.builder().customer(join).hpName("test2")
                                .severity(BigInteger.TWO).build();
                healthProblems.add(hpJoinFoo);
                healthProblems.add(hpJoin1Foo);

                Set<HealthProblem> healthProblems1 = new TreeSet<>(comparing(HealthProblem::getHpName));
                Customer join1 = Customer.builder().name("join1").dateBirth(LocalDate.parse("2001-03-05"))
                                .sex(Customer.Sex.NOT_KNOW).healthProblems(healthProblems1).build();
                HealthProblem hpJoinFoo1 = HealthProblem.builder().customer(join1).hpName("test1")
                                .severity(BigInteger.TWO).build();
                healthProblems1.add(hpJoinFoo1);

                repository.saveAllAndFlush(Set.of(foo, foo1, join, join1));
                healthProblemRepo.saveAllAndFlush(Set.of());
        }

        @Test
        void itShouldFindAllCustomersByName_WithLike() {
                List<Customer> customers = repository.findAllByNameLike("FOO");

                assertThat(customers).hasSize(2);
                assertThat(customers.get(0).getName()).isEqualTo("foO");
                assertThat(customers.get(1).getName()).isEqualTo("fOo1");
        }

        @Test
        void itShouldUpdateCustomer() {
                Customer customer = repository.findAll().get(1);
                UUID customerId = customer.getId();
                ZonedDateTime oldUpdatedAt = customer.getUpdatedAt();
                int oldCustomerHash = customer.hashCode();

                Customer update = Customer.builder().id(customerId).name("fooUpdated")
                                .dateBirth(LocalDate.parse("2000-04-30")).sex(Customer.Sex.FEMALE)
                                .healthProblems(new TreeSet<>()).build();
                repository.saveAndFlush(update);

                Customer updatedCustomer = repository.findById(customerId).get();
                assertThat(updatedCustomer).isNotNull();
                assertThat(updatedCustomer.hashCode()).isNotEqualTo(oldCustomerHash);
                assertThat(updatedCustomer.getUpdatedAt()).isNotEqualTo(oldUpdatedAt);
        }

        @Test
        void itShouldDeleteCustomer_AndRemoveHealthProblemsInCustomer() {
                List<Customer> customers = repository.findAll();
                Customer oldCustomer = customers.get(2);

                repository.delete(oldCustomer);

                assertThat(repository.findById(oldCustomer.getId())).isEmpty();
        }
}
