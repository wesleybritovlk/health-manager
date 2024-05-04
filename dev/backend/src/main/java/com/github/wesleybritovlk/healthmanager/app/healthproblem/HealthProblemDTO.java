package com.github.wesleybritovlk.healthmanager.app.healthproblem;

import java.math.BigInteger;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HealthProblemDTO {
        @Validated
        public static record Request(
                        @NotBlank(message = "Customer uuid is required")
                        @Size(min = 36, max = 36, message = "Customer uuid is invalid")
                        UUID customer_id,

                        @NotBlank(message = "Problem name shouldn't be null")
                        @Size(min = 3, max = 50, message = "Problem name must be greater than 3 and up to 50 characters")
                        String problem_name,

                        @NotBlank(message = "Invalid or null gender.\r\n" + 
                                        "Check: 'one' '1' or 'two' '2'") 
                        @Size(min = 3, max = 3, message = "Severity must have more than 1 up to 3 characters")
                        BigInteger severity) {
        }

        public static record Response(
                        UUID id,
                        UUID customer_id,
                        String problem_name,
                        BigInteger severity) {
        }
}
