package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

        @Query("select c from customer c where lower(c.name) " +
                        "like lower(concat('%', ?1,'%')) order by lower(c.name)")
        List<Customer> findAllByNameLike(String name);

        @Query("select coalesce(sum(hp.severity), 0) " +
                        "from health_problem hp where hp.customer.id = ?1")
        BigInteger findSeveritySumById(UUID id);
}
