package com.liccioni.school.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@RequiredArgsConstructor
@Table(name = "SCORES")
@SuperBuilder(toBuilder = true)
public class JpaScore extends BaseEntity {

    private String name;
    private Double score;
    @ManyToOne
    private JpaRegistration registration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaScore jpaScore)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(name, jpaScore.name)
                && Objects.equals(score, jpaScore.score)
                && Objects.equals(registration, jpaScore.registration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, score, registration);
    }
}
