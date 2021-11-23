package com.liccioni.school.http;

import com.liccioni.school.fixtures.CourseFixtures;
import com.liccioni.school.fixtures.StudentFixtures;
import com.liccioni.school.http.course.CourseController;
import com.liccioni.school.http.course.CreateCourseRequest;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import com.liccioni.school.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@WebFluxTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CourseService courseService;

    @Test
    void shouldListAllCourses() {

        Course math = CourseFixtures.math();
        Course history = CourseFixtures.history();
        when(courseService.findAll()).thenReturn(Flux.just(math, history));
        webTestClient.get().uri("/courses").exchange()
                .expectStatus().isOk()
                .expectBodyList(Course.class)
                .contains(math, history);
    }

    @Test
    void shouldCreateCourse() {
        CreateCourseRequest request = new CreateCourseRequest("math");
        Course math = CourseFixtures.math();
        when(courseService.save(argThat(request::equals))).thenReturn(Mono.just(math));
        webTestClient.post().uri("/courses")
                .bodyValue(new CreateCourseRequest("math")).exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/courses/".concat(math.id()));
    }

    @Test
    void shouldFindById() {

        Course math = CourseFixtures.math();
        when(courseService.findById(argThat(argument -> math.id().equals(argument)))).thenReturn(Mono.just(math));
        webTestClient.get().uri("/courses/{courseId}", math.id()).exchange()
                .expectStatus().isOk()
                .expectBody(Course.class)
                .isEqualTo(math);
    }

    @Test
    void shouldReturnStatusNotFound() {

        Course math = CourseFixtures.math();
        when(courseService.findById(argThat(argument -> math.id().equals(argument)))).thenReturn(Mono.empty());
        webTestClient.get().uri("/courses/{courseId}", math.id()).exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void shouldDeleteAllCourses() {
        webTestClient.delete().uri("/courses").exchange()
                .expectStatus().isNoContent().expectBody().isEmpty();
        verify(courseService).deleteAll();
    }

    @Test
    void shouldModifyCourse() {
        Course history = CourseFixtures.history();
        Course ancientHistoryRequest = new Course(null, "Ancient History");
        Course ancientHistory = new Course(history.id(), "Ancient History");
        when(courseService.modify(eq(history.id()), eq(ancientHistoryRequest))).thenReturn(Mono.just(ancientHistory));
        webTestClient.put().uri("/courses/{courseId}", history.id())
                .bodyValue(ancientHistoryRequest).exchange()
                .expectStatus().isOk()
                .expectBody(Course.class)
                .isEqualTo(ancientHistory);
    }

    @Test
    void shouldNotModifyCourseWhenNotFound() {
        Course history = CourseFixtures.history();
        Course ancientHistoryRequest = new Course(null, "Ancient History");
        when(courseService.modify(eq(history.id()), eq(ancientHistoryRequest))).thenReturn(Mono.empty());
        webTestClient.put().uri("/courses/{courseId}", history.id())
                .bodyValue(ancientHistoryRequest).exchange()
                .expectStatus().isNotFound().expectBody().isEmpty();
    }

    @Test
    void shouldDelete() {
        Course math = CourseFixtures.math();
        when(courseService.deleteById(eq(math.id()))).thenReturn(Mono.just(true));
        webTestClient.delete().uri("/courses/{courseId}", math.id()).exchange()
                .expectStatus().isNoContent().expectBody().isEmpty();
        verify(courseService).deleteById(eq(math.id()));
    }

    @Test
    void shouldNotDeleteWhenNotFound() {
        Course math = CourseFixtures.math();
        when(courseService.deleteById(eq(math.id()))).thenReturn(Mono.empty());
        webTestClient.delete().uri("/courses/{courseId}", math.id()).exchange()
                .expectStatus().isNotFound().expectBody().isEmpty();
        verify(courseService).deleteById(eq(math.id()));
    }

    @Test
    void shouldNotCreateCourseWhenNameIsDuplicated() {
        CreateCourseRequest request = new CreateCourseRequest("math");
        when(courseService.save(argThat(request::equals)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("duplicate name")));
        webTestClient.post().uri("/courses")
                .bodyValue(new CreateCourseRequest("math")).exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo("409")
                .jsonPath("$.message").isEqualTo("duplicate name");
    }

    @Test
    void shouldFindStudentsRegisteredInCourse() {
        Course science = CourseFixtures.science();
        Student kramer = StudentFixtures.kramer();
        Student elaine = StudentFixtures.elaine();
        when(courseService.findStudentsByCourseId(eq(science.id()))).thenReturn(Flux.just(kramer, elaine));
        webTestClient.get().uri("/courses/{id}/students", science.id()).exchange()
                .expectStatus().isOk()
                .expectBodyList(Student.class)
                .contains(kramer, elaine);
    }

    @Test
    void shouldFindStudentsNotTakingCourse() {
        Course science = CourseFixtures.science();
        Student kramer = StudentFixtures.kramer();
        Student elaine = StudentFixtures.elaine();
        when(courseService.findStudentsNotTakingCourse(eq(science.id()))).thenReturn(Flux.just(kramer, elaine));
        webTestClient.get().uri("/courses/{id}/students?not-in-course=true", science.id()).exchange()
                .expectStatus().isOk()
                .expectBodyList(Student.class)
                .contains(kramer, elaine);
    }
}
