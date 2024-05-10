package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerDTO {
        @Validated
        public static record Request(
                        @JsonProperty("full_name")
                        @NotNull(message = "Name shouldn't be null") 
                        @Size(min = 3, max = 50, message = "Name must be greater than 3 and up to 50 characters") 
                        String name,

                        @JsonProperty("date_birth")
                        @NotNull(message = "Invalid or null date of birth. " +
                                        "Format: 'yyyy-MM-dd'") 
                        LocalDate dateBirth,

                        @NotNull(message = "Invalid or null sex. " +
                                        "Enum Check: 'NOT_KNOW', 'MALE', 'FEMALE' or 'NOT_APPLICABLE'") 
                        Sex sex) {
        }

        public static record Response(
                        UUID id,
                        String full_name,
                        LocalDate date_birth,
                        Sex sex,
                        BigDecimal score,
                        Set<HealthProblemDTO.Response> health_problems) {
        }
}
