package com.liccioni.school.jpa;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "COURSES")
@SuperBuilder(toBuilder = true)
public class JpaCourse extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaCourse jpaCourse)) return false;
        if (!super.equals(o)) return false;
        return name.equals(jpaCourse.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
