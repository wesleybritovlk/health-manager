package com.github.wesleybritovlk.healthmanager.app.customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.github.wesleybritovlk.healthmanager.app.customer.Customer.Sex;
import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblemDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CustomerDTO {
        @Validated
        public static record Request(
                        @NotNull(message = "Full name shouldn't be null") 
                        @Size(min = 3, max = 50, message = "Full name must be greater than 3 and up to 50 characters") 
                        String full_name,

                        @NotNull(message = "Invalid or null date of birth.\r\n" +
                                        "Format: 'yyyy-MM-dd'") 
                        LocalDate date_birth,

                        @NotNull(message = "Invalid or null sex.\r\n" +
                                        "Check: 'not_know' '0', 'male' '1', 'female' '2' or 'not_applicable' '9'") 
                        @Size(min = 4, max = 14, message = "Sex must have more than 4 up to 14 characters") 
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
