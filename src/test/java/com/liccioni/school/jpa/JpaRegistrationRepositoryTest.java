package com.liccioni.school.jpa;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.jpa.repository.JpaRegistrationRepository;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaRegistrationRepositoryTest extends BaseRepositoryTest {

    private final RecursiveComparisonConfiguration ignoreCaseConfig = RecursiveComparisonConfiguration.builder()
            .withComparatorForFields((Comparator<String>) String::compareToIgnoreCase, "student.name", "student.lastName", "course.name")
            .build();

    @Autowired
    private JpaRegistrationRepository repository;

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql"},
            statements = {"TRUNCATE registrations RESTART IDENTITY CASCADE;"})
    void shouldSaveRegistration() {
        JpaStudent student = JpaStudent.builder().pk(1L).build();
        JpaCourse course = JpaCourse.builder().pk(1L).build();
        JpaRegistration registration = repository.save(JpaRegistration.builder().id("1").student(student).course(course).build());
        assertThat(registration.getPk()).isNotNull();
    }

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql", "classpath:jerry_registrations.sql"})
    void shouldFindRegistrationsByStudentId() {
        JpaStudent jerry = StudentFixtures.jpaJerry();
        List<JpaRegistration> registrations = repository.findByStudentId(jerry.getId());
        JpaCourse math = CourseFixtures.jpaMath();
        JpaCourse history = CourseFixtures.jpaHistory();
        JpaRegistration mathRegistration = JpaRegistration.builder().pk(1L).id("1").student(jerry).course(math).build();
        JpaRegistration historyRegistration = JpaRegistration.builder().pk(2L).id("2").student(jerry).course(history).build();
        assertThat(registrations)
                .usingRecursiveFieldByFieldElementComparator(ignoreCaseConfig)
                .containsExactlyInAnyOrder(mathRegistration, historyRegistration);
    }

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql", "classpath:jerry_registrations.sql"})
    void shouldNotRegisterTwice() {
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(JpaRegistration.builder()
                .id("3").course(CourseFixtures.jpaMath()).student(StudentFixtures.jpaJerry())
                .build()));
    }

    @Test
    @Sql(scripts = {"classpath:courses.sql", "classpath:students.sql", "classpath:jerry_registrations.sql"})
    void shouldSubmitScores() {
        JpaStudent jerry = StudentFixtures.jpaJerry();
        JpaCourse math = CourseFixtures.jpaMath();
        JpaRegistration registration = repository.findByStudentId(jerry.getId()).stream()
                .filter(jpaRegistration -> jpaRegistration.getCourse().equals(math)).findFirst().orElseThrow();
        JpaScore test1 = JpaScore.builder().id("1").name("test1").registration(registration).score(15.3).build();
        registration.addScore(test1);
        JpaScore test2 = JpaScore.builder().id("2").name("test2").registration(registration).score(4.5).build();
        registration.addScore(test2);
        JpaRegistration actual = repository.save(registration);
        assertThat(actual.getScores()).usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields("pk")
                        .build())
                .containsExactlyInAnyOrder(test1, test2);
    }
}