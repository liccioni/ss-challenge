package com.liccioni.school.jpa;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "REGISTRATIONS",
        uniqueConstraints = {@UniqueConstraint(name = "idx_unique_student_and_course",
                columnNames = {"student_pk", "course_pk"})})
@NamedEntityGraph(name = "registration.graph",
        attributeNodes = {@NamedAttributeNode("student"), @NamedAttributeNode("course"), @NamedAttributeNode("scores")})
@SuperBuilder(toBuilder = true)
public class JpaRegistration extends BaseEntity {

    @ManyToOne(cascade = CascadeType.MERGE)
    private JpaStudent student;
    @ManyToOne(cascade = CascadeType.MERGE)
    private JpaCourse course;
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL)
    private Set<JpaScore> scores = new HashSet<>();

    public void addScore(JpaScore score) {
        scores.add(score);
    }

    public Set<JpaScore> getScores() {
        return Collections.unmodifiableSet(scores);
    }
}
