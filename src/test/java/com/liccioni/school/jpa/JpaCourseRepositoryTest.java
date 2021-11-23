package com.liccioni.school.jpa;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.jpa.repository.JpaCourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaCourseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JpaCourseRepository repository;

    @Test
    @Sql(scripts = "classpath:courses.sql")
    void shouldFindAll() {

        JpaCourse jpaMath = CourseFixtures.jpaMath();
        JpaCourse jpaHistory = CourseFixtures.jpaHistory();
        JpaCourse jpaScience = CourseFixtures.jpaScience();
        JpaCourse jpaEnglish = CourseFixtures.jpaEnglish();
        assertThat(repository.findAll()).containsExactlyInAnyOrder(jpaMath, jpaHistory, jpaScience, jpaEnglish);
    }

    @Test
    @Sql(scripts = "classpath:courses.sql")
    void shouldDeleteById() {
        assertThat(repository.deleteById("2")).isEqualTo(1);
    }

    @Test
    @Sql(scripts = "classpath:courses.sql")
    void shouldNotDeleteById() {
        assertThat(repository.deleteById("123456")).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "classpath:courses.sql")
    void shouldNotCreateDuplicateName() {

        assertThrows(DataIntegrityViolationException.class, () -> repository.save(JpaCourse.builder().id("2").name("math").build()));
    }

    @Test
    @Sql(scripts = "classpath:courses.sql")
    void shouldFindCoursesByIds() {
        List<JpaCourse> coursesByIds = repository.findByIdIn(List.of(CourseFixtures.math().id(), CourseFixtures.history().id()));
        assertThat(coursesByIds).containsExactlyInAnyOrder(CourseFixtures.jpaMath(), CourseFixtures.jpaHistory());
    }
}
