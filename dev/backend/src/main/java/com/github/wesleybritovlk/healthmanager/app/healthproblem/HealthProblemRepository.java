package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthProblemRepository
        extends JpaRepository<HealthProblem, UUID> {
}
