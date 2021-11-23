package com.liccioni.school.http;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.http.student.*;
import com.liccioni.school.model.*;
import com.liccioni.school.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StudentService studentService;

    @Test
    void shouldDeleteAllStudents() {
        webTestClient.delete().uri("/students").exchange()
                .expectStatus().isNoContent().expectBody().isEmpty();
        verify(studentService).deleteAll();
    }

    @Test
    void shouldGetAllStudents() {
        when(studentService.findAll()).thenReturn(Flux.just(StudentFixtures.all()));
        webTestClient.get().uri("/students").exchange()
                .expectStatus().isOk()
                .expectBodyList(Student.class)
                .contains(StudentFixtures.all());
    }

    @Test
    void shouldCreateStudent() {
        Student jerry = StudentFixtures.jerry();
        when(studentService.save(eq(new CreateStudentRequest(jerry.studentId(), jerry.name(), jerry.lastName()))))
                .thenReturn(Mono.just(jerry));
        webTestClient.post().uri("/students").bodyValue(jerry).exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/students/".concat(jerry.id()));
    }

    @Test
    void shouldFindStudentById() {
        Student kramer = StudentFixtures.kramer();
        when(studentService.findById(eq(kramer.id()))).thenReturn(Mono.just(kramer));
        webTestClient.get().uri("/students/{id}", kramer.id()).exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .isEqualTo(kramer);
    }

    @Test
    void shouldNotFindStudentById() {
        Student kramer = StudentFixtures.kramer();
        when(studentService.findById(eq(kramer.id()))).thenReturn(Mono.empty());
        webTestClient.get().uri("/students/{id}", kramer.id()).exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void shouldModifyStudent() {
        Student george = StudentFixtures.george();
        Student larry = new Student(george.id(), george.studentId(), "Larry", "David");
        when(studentService.modify(eq(george.id()), eq(larry))).thenReturn(Mono.just(larry));
        webTestClient.put().uri("/students/{id}", george.id()).bodyValue(larry).exchange()
                .expectStatus().isOk()
                .expectBody(Student.class)
                .isEqualTo(larry);
    }

    @Test
    void shouldNotModifyStudent() {
        Student george = StudentFixtures.george();
        Student larry = new Student(george.id(), george.studentId(), "Larry", "David");
        when(studentService.modify(eq(george.id()), eq(larry))).thenReturn(Mono.empty());
        webTestClient.put().uri("/students/{id}", george.id()).bodyValue(larry).exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void shouldDeleteStudent() {
        Student kramer = StudentFixtures.kramer();
        when(studentService.deleteById(eq(kramer.id()))).thenReturn(Mono.just(true));
        webTestClient.delete().uri("/students/{id}", kramer.id()).exchange()
                .expectStatus().isNoContent().expectBody().isEmpty();
    }

    @Test
    void shouldNotDeleteStudentWhenNotFound() {
        Student kramer = StudentFixtures.kramer();
        when(studentService.deleteById(eq(kramer.id()))).thenReturn(Mono.empty());
        webTestClient.delete().uri("/students/{id}", kramer.id()).exchange()
                .expectStatus().isNotFound().expectBody().isEmpty();
    }

    @Test
    void shouldRegisterCourses() {
        Student elaine = StudentFixtures.elaine();
        Course math = CourseFixtures.math();
        Course history = CourseFixtures.history();
        List<Registration> expected = List.of(new Registration("1", elaine, math), new Registration("2", elaine, history));
        when(studentService.register(eq(elaine.id()), eq(List.of(math.id(), history.id())))).thenReturn(Flux.fromIterable(expected));
        webTestClient.post().uri("/students/{id}/registrations", elaine.id())
                .bodyValue(new CreateRegistrationRequest(List.of(math.id(), history.id())))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/students/" + elaine.id() + "/registrations");
    }

    @Test
    void shouldGetStudentRegistrations() {
        Student newman = StudentFixtures.newman();
        Course math = CourseFixtures.math();
        Course science = CourseFixtures.science();
        List<CourseRegistration> registrations = List.of(new CourseRegistration("1", math), new CourseRegistration("2", science));
        StudentRegistrations expected = new StudentRegistrations(newman, registrations);
        when(studentService.findRegistrations(eq(newman.id()))).thenReturn(Mono.just(expected));
        webTestClient.get().uri("/students/{id}/registrations", newman.id()).exchange()
                .expectStatus().isOk()
                .expectBody(StudentRegistrations.class)
                .isEqualTo(expected);
    }

    @Test
    void shouldSubmitScores() {
        Student kramer = StudentFixtures.kramer();
        String registrationId = "1";
        CreateScoreRequest test1 = new CreateScoreRequest("Test1", 8.9);
        CreateScoreRequest test2 = new CreateScoreRequest("Test2", 10);
        CreateScoresRequest request = new CreateScoresRequest(List.of(test1, test2));
        Score scoreTest1 = new Score("1", test1.name(), test1.score());
        Score scoreTest2 = new Score("2", test2.name(), test2.score());
        when(studentService.submitScores(eq(registrationId), eq(request.scores()))).thenReturn(Flux.just(scoreTest1, scoreTest2));
        webTestClient.post().uri("/students/{id}/registrations/{registrationId}/scores", kramer.id(), registrationId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/students/" + kramer.id() + "/registrations/" + registrationId + "/scores");
    }

    @Test
    void shouldGetScores() {
        Student kramer = StudentFixtures.kramer();
        String registrationId = "1";
        Course math = CourseFixtures.math();
        Score scoreTest1 = new Score("1", "test1", 10.0);
        Score scoreTest2 = new Score("2", "test2", 8.9);
        RegistrationScores expected = new RegistrationScores(kramer, math, List.of(scoreTest1, scoreTest2));
        when(studentService.findScores(eq(registrationId))).thenReturn(Mono.just(expected));
        webTestClient.get().uri("/students/{id}/registrations/{registrationId}/scores", kramer.id(), registrationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationScores.class)
                .isEqualTo(expected);
    }
}