package com.liccioni.school.service;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.http.student.CreateScoreRequest;
import com.liccioni.school.http.student.CreateScoresRequest;
import com.liccioni.school.http.student.CreateStudentRequest;
import com.liccioni.school.jpa.JpaCourse;
import com.liccioni.school.jpa.JpaRegistration;
import com.liccioni.school.jpa.JpaScore;
import com.liccioni.school.jpa.JpaStudent;
import com.liccioni.school.jpa.repository.JpaCourseRepository;
import com.liccioni.school.jpa.repository.JpaRegistrationRepository;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.model.*;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultStudentServiceTest {

    private StudentService service;

    @Mock
    private JpaStudentRepository repository;

    @Mock
    private JpaRegistrationRepository registrationRepository;

    @Mock
    private JpaCourseRepository courseRepository;

    @Mock
    private Supplier<String> idGenerator;

    @BeforeEach
    void setUp() {
        service = new DefaultStudentService(repository, registrationRepository, courseRepository, idGenerator);
    }

    @Test
    void deleteAll() {
        service.deleteAll();
        verify(repository).deleteAll();
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(StudentFixtures.allJpa());
        StepVerifier.create(service.findAll())
                .expectNext(StudentFixtures.all())
                .verifyComplete();
    }

    @Test
    void save() {

        JpaStudent jpaRequest = StudentFixtures.jpaGeorge().toBuilder().pk(null).build();
        when(idGenerator.get()).thenReturn("2");
        when(repository.save(eq(jpaRequest))).thenReturn(StudentFixtures.jpaGeorge());
        StepVerifier.create(service.save(new CreateStudentRequest("S002", "George", "Costanza")))
                .expectNext(StudentFixtures.george())
                .verifyComplete();
    }

    @Test
    void findById() {
        when(repository.findById(eq(StudentFixtures.elaine().id()))).thenReturn(Optional.of(StudentFixtures.jpaElaine()));
        StepVerifier.create(service.findById(StudentFixtures.elaine().id()))
                .expectNext(StudentFixtures.elaine())
                .verifyComplete();
    }


    @Test
    void modify() {
        Student george = StudentFixtures.george();
        Student larry = new Student(george.id(), george.studentId(), "Larry", "David");
        JpaStudent jpaLarry = StudentFixtures.jpaGeorge().toBuilder().name(larry.name()).lastName(larry.lastName()).build();
        when(repository.findById(eq(george.id()))).thenReturn(Optional.of(StudentFixtures.jpaGeorge()));
        when(repository.save(eq(jpaLarry))).thenReturn(jpaLarry);
        StepVerifier.create(service.modify(george.id(), larry))
                .expectNext(larry)
                .verifyComplete();
    }

    @Test
    void delete() {
        Student kramer = StudentFixtures.kramer();
        when(repository.deleteById(eq(kramer.id()))).thenReturn(1L);
        StepVerifier.create(service.deleteById(kramer.id()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void register() {
        Student elaine = StudentFixtures.elaine();
        Course math = CourseFixtures.math();
        Course history = CourseFixtures.history();
        Registration mathRegistration = new Registration("1", elaine, math);
        Registration historyRegistration = new Registration("2", elaine, history);
        JpaCourse jpaMath = CourseFixtures.jpaMath();
        JpaCourse jpaHistory = CourseFixtures.jpaHistory();
        when(courseRepository.findByIdIn(eq(List.of(math.id(), history.id())))).thenReturn(List.of(jpaMath, jpaHistory));
        JpaStudent jpaElaine = StudentFixtures.jpaElaine();
        when(repository.findById(eq(elaine.id()))).thenReturn(Optional.of(jpaElaine));
        when(idGenerator.get()).thenReturn("1", "2");
        JpaRegistration jpaMathRegistration = JpaRegistration.builder().pk(1L).id("1").student(jpaElaine).course(jpaMath).build();
        JpaRegistration jpaHistoryRegistration = JpaRegistration.builder().pk(2L).id("2").student(jpaElaine).course(jpaHistory).build();
        when(registrationRepository.save(any())).thenReturn(jpaMathRegistration, jpaHistoryRegistration);
        StepVerifier.create(service.register(elaine.id(), List.of(math.id(), history.id())))
                .expectNext(mathRegistration, historyRegistration)
                .verifyComplete();
    }

    @Test
    void findRegistrations() {
        Student newman = StudentFixtures.newman();
        Course english = CourseFixtures.english();
        Course science = CourseFixtures.science();
        List<CourseRegistration> registrations = List.of(new CourseRegistration("1", english), new CourseRegistration("2", science));
        StudentRegistrations expected = new StudentRegistrations(newman, registrations);
        JpaStudent jpaNewman = StudentFixtures.jpaNewman();
        JpaCourse jpaEnglish = CourseFixtures.jpaEnglish();
        JpaCourse jpaScience = CourseFixtures.jpaScience();
        JpaRegistration englishRegistration = JpaRegistration.builder().pk(1L).id("1").student(jpaNewman).course(jpaEnglish).build();
        JpaRegistration scienceRegistration = JpaRegistration.builder().pk(2L).id("2").student(jpaNewman).course(jpaScience).build();
        when(registrationRepository.findByStudentId(eq(newman.id()))).thenReturn(List.of(englishRegistration, scienceRegistration));
        StepVerifier.create(service.findRegistrations(newman.id()))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void submitScores() {
        String registrationId = "1";
        CreateScoreRequest test1 = new CreateScoreRequest("Test1", 8.9);
        CreateScoreRequest test2 = new CreateScoreRequest("Test2", 10);
        CreateScoresRequest request = new CreateScoresRequest(List.of(test1, test2));
        Score scoreTest1 = new Score("1", test1.name(), test1.score());
        Score scoreTest2 = new Score("2", test2.name(), test2.score());

        JpaRegistration jpaRegistration = JpaRegistration.builder()
                .pk(1L)
                .id(registrationId).student(StudentFixtures.jpaKramer()).course(CourseFixtures.jpaMath())
                .build();

        when(registrationRepository.findById(eq(registrationId))).thenReturn(Optional.of(jpaRegistration));
        when(idGenerator.get()).thenReturn("1", "2");
        when(registrationRepository.save(eq(jpaRegistration))).thenReturn(jpaRegistration);

        StepVerifier.create(service.submitScores(registrationId, request.scores()))
                .expectNext(scoreTest1, scoreTest2)
                .verifyComplete();
    }

    @Test
    void getScores() {
        String registrationId = "1";
        Score scoreTest1 = new Score("1", "test1", 10.0);
        Score scoreTest2 = new Score("2", "test2", 8.9);
        Student kramer = StudentFixtures.kramer();
        Course math = CourseFixtures.math();
        RegistrationScores expected = new RegistrationScores(kramer, math, List.of(scoreTest1, scoreTest2));
        JpaScore jpaTestScore1 = JpaScore.builder().id(scoreTest1.id())
                .name(scoreTest1.name()).score(scoreTest1.score()).build();
        JpaScore jpaTestScore2 = JpaScore.builder().id(scoreTest2.id())
                .name(scoreTest2.name()).score(scoreTest2.score()).build();
        JpaRegistration jpaRegistration = JpaRegistration.builder()
                .pk(1L).id(registrationId).student(StudentFixtures.jpaKramer()).course(CourseFixtures.jpaMath())
                .scores(Set.of(jpaTestScore1, jpaTestScore2))
                .build();
        when(registrationRepository.findById(eq(registrationId))).thenReturn(Optional.of(jpaRegistration));
        StepVerifier.create(service.findScores(registrationId))
                .consumeNextWith(actual ->
                        assertThat(actual).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                                        .withIgnoreCollectionOrder(true)
                                        .build())
                                .isEqualTo(expected))
                .verifyComplete();
    }
}