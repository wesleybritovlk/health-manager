package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

        @Query("select c from customer c where lower(c.fullName) " +
                        "like lower(concat('%', :fullName,'%')) order by lower(c.fullName)")
        List<Customer> findAllByFullNameLike(@Param("fullName") String fullName);

        @Query("select coalesce(sum(hp.severity), 0) " +
                        "from health_problem hp where hp.customer.id = :id")
        BigInteger findSeveritySumById(@Param("id") UUID id);
}
