package com.liccioni.school.jpa;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.model.Student;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JpaStudentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JpaStudentRepository repository;
    private final RecursiveComparisonConfiguration optionalConfig = RecursiveComparisonConfiguration.builder()
            .withComparatorForFields((Comparator<String>) String::compareToIgnoreCase, "value.name", "value.lastName")
            .build();
    private final RecursiveComparisonConfiguration ignoreCaseConfig = RecursiveComparisonConfiguration.builder()
            .withComparatorForFields((Comparator<String>) String::compareToIgnoreCase, "name", "lastName")
            .build();

    @Test
    @Sql(scripts = "classpath:students.sql")
    void shouldDeleteAll() {
        assertThat(repository.findAll()).isNotEmpty();
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @Sql(scripts = "classpath:students.sql")
    void shouldFindAll() {
        assertThat(repository.findAll())
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder()
                        .withComparatorForFields((Comparator<String>) String::compareToIgnoreCase, "name", "lastName")
                        .build())
                .containsExactlyInAnyOrder(StudentFixtures.allJpa().toArray(JpaStudent[]::new));
    }

    @Test
    @Sql(scripts = "classpath:students.sql")
    void shouldFindById() {
        JpaStudent jpaNewman = StudentFixtures.jpaNewman();
        assertThat(repository.findById(jpaNewman.getId()))
                .usingRecursiveComparison(optionalConfig)
                .isEqualTo(Optional.of(jpaNewman));
    }

    @Test
    @Sql(scripts = "classpath:students.sql")
    void shouldModifyStudent() {
        JpaStudent jpaGeorge = StudentFixtures.jpaGeorge();
        JpaStudent jpaLarry = jpaGeorge.toBuilder().name("Larry").lastName("David").build();
        repository.save(jpaLarry);
        assertThat(repository.findById(jpaGeorge.getId()))
                .usingRecursiveComparison(optionalConfig)
                .isEqualTo(Optional.of(jpaLarry));
    }

    @Test
    @Sql(scripts = "classpath:students.sql")
    void shouldDeleteStudent() {
        Student kramer = StudentFixtures.kramer();
        repository.deleteById(kramer.id());
        assertThat(repository.findById(kramer.id())).isNotPresent();
    }

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql", "classpath:history_registrations.sql"})
    void shouldFindStudentsByCourseId() {
        JpaCourse history = CourseFixtures.jpaHistory();
        JpaStudent jerry = StudentFixtures.jpaJerry();
        JpaStudent george = StudentFixtures.jpaGeorge();
        assertThat(repository.findStudentsTakingCourse(history.getId()))
                .usingRecursiveFieldByFieldElementComparator(ignoreCaseConfig)
                .containsExactlyInAnyOrder(jerry, george);
    }

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql", "classpath:science_registrations.sql"})
    void shouldFindStudentsNotTakingCourse() {
        JpaCourse science = CourseFixtures.jpaScience();
        JpaStudent george = StudentFixtures.jpaGeorge();
        JpaStudent kramer = StudentFixtures.jpaKramer();
        assertThat(repository.findStudentsNotTakingCourse(science.getId()))
                .usingRecursiveFieldByFieldElementComparator(ignoreCaseConfig)
                .containsExactlyInAnyOrder(george, kramer);
    }
}