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
@Table(name = "STUDENTS")
@SuperBuilder(toBuilder = true)
public class JpaStudent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String studentId;
    private String name;
    private String lastName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaStudent that)) return false;
        if (!super.equals(o)) return false;
        return studentId.equals(that.studentId) && name.equals(that.name) && lastName.equals(that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentId, name, lastName);
    }
}
