package com.github.wesleybritovlk.healthmanager.app.customer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import com.github.wesleybritovlk.healthmanager.app.healthproblem.HealthProblem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "customer")
public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    @Length(min = 3, max = 50)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 14)
    private Sex sex;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<HealthProblem> healthProblems;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    /** ISO/IEC 5218 https://en.wikipedia.org/wiki/ISO/IEC_5218 */
    @AllArgsConstructor
    public enum Sex {
        NOT_KNOW(0),
        MALE(1),
        FEMALE(2),
        NOT_APPLICABLE(9);

        @Getter
        Integer code;
    }
}
