package com.liccioni.school.jpa;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@MappedSuperclass
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false)
    private Long pk;

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return Objects.equals(pk, that.pk) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, id);
    }
}