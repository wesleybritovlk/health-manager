package com.github.wesleybritovlk.healthmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "${app.name}", version = "${app.version}"))
@RequiredArgsConstructor
public class HealthmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthmanagerApplication.class, args);
	}

}
