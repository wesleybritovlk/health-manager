package com.github.wesleybritovlk.healthmanager.handler;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalHandlerRepository extends JpaRepository<GlobalHandler, UUID> {
}
