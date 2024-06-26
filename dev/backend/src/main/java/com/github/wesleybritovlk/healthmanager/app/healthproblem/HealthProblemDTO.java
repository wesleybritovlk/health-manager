package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import java.math.BigInteger;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthProblemDTO {
        @Validated
        @Schema(name = "HealthProblemRequest", title = "HealthProblemRequest")
        public static record Request(
                        @JsonProperty("customer_id")
                        @NotNull(message = "Customer uuid is required")
                        UUID customerId,

                        @JsonProperty("problem_name")
                        @NotBlank(message = "Problem name shouldn't be null")
                        @Size(min = 3, max = 50, message = "Problem name must be greater than 3 and up to 50 characters")
                        String hpName,

                        @NotNull(message = "Invalid or null severity. " + 
                                        "Severity Check: '1' or '2'") 
                        BigInteger severity) {
        }

        @Schema(name = "HealthProblemResponse", title = "HealthProblemResponse")
        public static record Response(
                        UUID id,
                        UUID customer_id,
                        String problem_name,
                        BigInteger severity) {
        }
}
