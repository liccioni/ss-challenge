package com.liccioni.school.service;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.http.course.CreateCourseRequest;
import com.liccioni.school.jpa.JpaCourse;
import com.liccioni.school.jpa.JpaStudent;
import com.liccioni.school.jpa.repository.JpaCourseRepository;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCourseServiceTest {

    private DefaultCourseService service;

    @Mock
    private JpaCourseRepository repository;

    @Mock
    private JpaStudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        service = new DefaultCourseService(repository, studentRepository, () -> "1");
    }

    @Test
    void findAll() {

        JpaCourse mathJpa = CourseFixtures.jpaMath();
        JpaCourse historyJpa = CourseFixtures.jpaHistory();
        when(repository.findAll()).thenReturn(List.of(mathJpa, historyJpa));
        StepVerifier.create(service.findAll())
                .expectNext(CourseFixtures.math(), CourseFixtures.history())
                .verifyComplete();
    }

    @Test
    void create() {
        JpaCourse mathJpa = CourseFixtures.jpaMath();
        JpaCourse mathJpaRequest = mathJpa.toBuilder().pk(null).build();
        when(repository.save(eq(mathJpaRequest))).thenReturn(mathJpa);
        StepVerifier.create(service.save(new CreateCourseRequest("math")))
                .expectNext(CourseFixtures.math())
                .verifyComplete();
    }

    @Test
    void findById() {
        Course math = CourseFixtures.math();
        when(repository.findById(eq(math.id())))
                .thenReturn(Optional.of(CourseFixtures.jpaMath()));
        StepVerifier.create(service.findById(math.id()))
                .expectNext(math)
                .verifyComplete();
    }

    @Test
    void notFindById() {
        when(repository.findById(eq("1")))
                .thenReturn(Optional.empty());
        StepVerifier.create(service.findById("1"))
                .verifyComplete();
    }

    @Test
    void deleteAll() {
        service.deleteAll();
        verify(repository).deleteAll();
    }

    @Test
    void modify() {
        Course history = CourseFixtures.history();
        Course ancientHistoryRequest = new Course(null, "Ancient History");
        JpaCourse jpaHistory = CourseFixtures.jpaHistory();
        JpaCourse jpaAncientHistory = jpaHistory.toBuilder().name(ancientHistoryRequest.name()).build();
        when(repository.findById(eq(history.id()))).thenReturn(Optional.of(jpaHistory));
        when(repository.save(eq(jpaAncientHistory))).thenReturn(jpaAncientHistory);
        Course expected = new Course(history.id(), ancientHistoryRequest.name());
        StepVerifier.create(service.modify(history.id(), ancientHistoryRequest))
                .expectNext(expected)
                .verifyComplete();
        verify(repository).save(argThat(jpaAncientHistory::equals));
    }

    @Test
    void deleteById() {
        Course math = CourseFixtures.math();
        when(repository.deleteById(eq(math.id()))).thenReturn(1);
        StepVerifier.create(service.deleteById(math.id()))
                .expectNext(true)
                .verifyComplete();
        verify(repository).deleteById(eq(math.id()));
    }

    @Test
    void findStudentsByCourseId() {
        Course science = CourseFixtures.science();
        Student kramer = StudentFixtures.kramer();
        Student elaine = StudentFixtures.elaine();
        JpaStudent jpaKramer = StudentFixtures.jpaKramer();
        JpaStudent jpaElaine = StudentFixtures.jpaElaine();
        when(studentRepository.findStudentsTakingCourse(eq(science.id()))).thenReturn(List.of(jpaKramer, jpaElaine));
        StepVerifier.create(service.findStudentsByCourseId(science.id()))
                .expectNext(kramer, elaine)
                .verifyComplete();
    }

    @Test
    void findStudentsNotTakingCourse() {
        Student kramer = StudentFixtures.kramer();
        Student newman = StudentFixtures.newman();
        Course english = CourseFixtures.english();
        JpaStudent jpaKramer = StudentFixtures.jpaKramer();
        JpaStudent jpaNewman = StudentFixtures.jpaNewman();
        when(studentRepository.findStudentsNotTakingCourse(eq(english.id()))).thenReturn(List.of(jpaKramer, jpaNewman));
        StepVerifier.create(service.findStudentsNotTakingCourse(english.id()))
                .expectNext(kramer, newman)
                .verifyComplete();
    }
}