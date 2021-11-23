package com.liccioni.school.bdd.client;

import com.liccioni.school.bdd.steps.CreateStudentRegistrationRequest;
import com.liccioni.school.bdd.steps.ScoreRequest;
import com.liccioni.school.http.course.CreateCourseRequest;
import com.liccioni.school.http.student.CreateScoresRequest;
import com.liccioni.school.http.student.CreateStudentRequest;
import com.liccioni.school.model.*;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationWebClient implements ApplicationClient {
    public static final String COURSES_PATH = "/courses";

    public static final String STUDENTS_PATH = "/students";
    public static final String STUDENTS_PATH_ID = "/students/{id}";
    public static final String COURSE_PATH_ID = "/courses/{courseId}";
    private final WebTestClient webTestClient;
    private final Map<Class<?>, Map<String, Object>> mainCache = new ConcurrentHashMap<>();


    public ApplicationWebClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
        mainCache.put(Student.class, new ConcurrentHashMap<>());
        mainCache.put(Course.class, new ConcurrentHashMap<>());
    }

    @Override
    public void deleteAllCourses() {
        deleteAll(COURSES_PATH, Course.class);
    }

    @Override
    public List<Course> getAllCourses() {
        return getAll(COURSES_PATH, Course.class);
    }

    @Override
    public Course createCourseWithName(String courseName) {
        return create(COURSES_PATH, new CreateCourseRequest(courseName), Course.class);
    }

    @Override
    public Course findCourseById(String id) {
        return findById(COURSE_PATH_ID, id, Course.class);
    }

    @Override
    public Course modifyCourse(String oldName, String newName) {
        Course course = getCachedCourse(oldName).orElseThrow();
        return modify(COURSE_PATH_ID, course.id(), new Course(null, newName), Course.class);
    }

    @Override
    public boolean deleteCourse(String id) {
        return delete(id, Course.class, COURSE_PATH_ID);
    }

    @Override
    public void deleteAllStudents() {
        deleteAll(STUDENTS_PATH, Student.class);
    }

    @Override
    public List<Student> getAllStudents() {
        return getAll(STUDENTS_PATH, Student.class);
    }

    @Override
    public Student createStudent(Student student) {
        CreateStudentRequest request = new CreateStudentRequest(student.studentId(), student.name(), student.lastName());
        return create(STUDENTS_PATH, request, Student.class);
    }

    @Override
    public Student findStudentById(String id) {
        return findById(STUDENTS_PATH_ID, id, Student.class);
    }

    @Override
    public Student modifyStudent(Student newDetails) {
        String id = getCachedStudent(newDetails.studentId()).map(Student::id).orElse(null);
        return modify(STUDENTS_PATH_ID, id, newDetails, Student.class);
    }

    @Override
    public boolean deleteStudent(String id) {
        return delete(id, Student.class, STUDENTS_PATH_ID);
    }

    @Override
    public StudentRegistrations createStudentRegistration(CreateStudentRegistrationRequest request) {
        String newLocation = webTestClient.post()
                .uri("/students/{id}/registrations", request.studentId()).bodyValue(request).exchange()
                .expectStatus().isCreated()
                .returnResult(Void.class).getResponseHeaders().getFirst("Location");
        return getRegistrations(newLocation);
    }

    @Override
    public List<Student> getStudentsByCourseId(String id) {
        return webTestClient.get().uri("/courses/{id}/students", id).exchange()
                .expectStatus().isOk()
                .expectBodyList(Student.class)
                .returnResult().getResponseBody();
    }

    @Override
    public List<Student> getStudentsNotInCourseByCourseId(String id) {
        return webTestClient.get().uri("/courses/{id}/students?not-in-course=true", id).exchange()
                .expectStatus().isOk()
                .expectBodyList(Student.class)
                .returnResult().getResponseBody();
    }

    @Override
    public Optional<Course> getCachedCourse(String courseName) {
        return Optional.of(mainCache)
                .map(cache -> cache.get(Course.class))
                .map(cache -> cache.get(courseName))
                .map(Course.class::cast);
    }

    @Override
    public RegistrationScores submitScores(ScoreRequest scoreRequest) {
        String studentId = scoreRequest.student().id();
        String courseId = scoreRequest.course().id();
        String registrationId = this.getRegistrations(String.format("/students/%s/registrations", studentId)).registrations()
                .stream().filter(p -> p.course().id().equals(courseId)).map(CourseRegistration::id).findFirst().orElseThrow();
        String newLocation = webTestClient.post().uri("/students/{id}/registrations/{registrationId}/scores", studentId, registrationId)
                .bodyValue(new CreateScoresRequest(scoreRequest.score()))
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Void.class).getResponseHeaders().getFirst("Location");
        return webTestClient.get().uri(Objects.requireNonNull(newLocation)).exchange()
                .expectStatus().isOk().expectBody(RegistrationScores.class).returnResult().getResponseBody();
    }

    @Override
    public RegistrationScores getScores(ScoreRequest scoreRequest) {
        String studentId = scoreRequest.student().id();
        String courseId = scoreRequest.course().id();
        String registrationId = this.getRegistrations(String.format("/students/%s/registrations", studentId)).registrations()
                .stream().filter(p -> p.course().id().equals(courseId)).map(CourseRegistration::id).findFirst().orElseThrow();
        return webTestClient.get().uri("/students/{id}/registrations/{registrationId}/scores", studentId, registrationId)
                .exchange()
                .expectStatus().isOk().expectBody(RegistrationScores.class).returnResult().getResponseBody();
    }

    @Override
    public Optional<Student> getCachedStudent(String studentId) {

        return Optional.of(mainCache)
                .map(cache -> cache.get(Student.class))
                .map(cache -> cache.get(studentId))
                .map(Student.class::cast);
    }

    private <T> T create(String path, Object body, Class<T> type) {
        String newLocation = webTestClient.post().uri(path).bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Void.class).getResponseHeaders().getFirst("Location");
        T responseBody = webTestClient.get().uri(Objects.requireNonNull(newLocation)).exchange()
                .expectStatus().isOk().expectBody(type).returnResult().getResponseBody();
        resolveCache(type).put(getKey(responseBody), responseBody);
        return responseBody;
    }

    private void deleteAll(String path, Class<?> type) {
        webTestClient.delete().uri(path).exchange().expectStatus().isNoContent().expectBody().isEmpty();
        mainCache.get(type).clear();
    }

    private <T> List<T> getAll(String path, Class<T> type) {
        Map<String, Object> cache = resolveCache(type);
        cache.clear();
        List<T> responseBody = webTestClient.get().uri(path).exchange()
                .expectStatus().isOk()
                .expectBodyList(type)
                .returnResult().getResponseBody();
        Objects.requireNonNull(responseBody).forEach(s -> cache.put(getKey(s), s));
        return responseBody;
    }

    private <T> T findById(String path, String id, Class<T> type) {
        T entity = webTestClient.get().uri(path, id).exchange()
                .expectStatus().isOk().expectBody(type)
                .returnResult().getResponseBody();
        mainCache.get(type).put(getKey(entity), entity);
        return entity;
    }

    private <T> boolean delete(String id, Class<T> type, String path) {
        return webTestClient.delete().uri(path, id).exchange()
                .expectStatus().isNoContent().expectBody().isEmpty()
                .getStatus().is2xxSuccessful();
    }

    private <T> T modify(String path, String id, T newDetails, Class<T> type) {
        T entity = webTestClient.put().uri(path, id).bodyValue(newDetails).exchange()
                .expectStatus().isOk()
                .expectBody(type)
                .returnResult().getResponseBody();
        mainCache.get(type).put(getKey(entity), entity);
        return entity;
    }

    private StudentRegistrations getRegistrations(String newLocation) {
        return webTestClient.get().uri(Objects.requireNonNull(newLocation)).exchange()
                .expectStatus().isOk().expectBody(StudentRegistrations.class).returnResult().getResponseBody();
    }

    private Map<String, Object> resolveCache(Class<?> type) {
        return mainCache.get(type);
    }

    private <T> String getKey(T o) {
        return switch (o) {
            case Student s -> s.studentId();
            case Course c -> c.name();
            default -> throw new IllegalStateException("Unexpected value: " + o);
        };
    }
}
