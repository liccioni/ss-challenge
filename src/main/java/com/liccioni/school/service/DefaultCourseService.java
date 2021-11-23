package com.liccioni.school.service;

import com.liccioni.school.http.course.CreateCourseRequest;
import com.liccioni.school.jpa.JpaCourse;
import com.liccioni.school.jpa.JpaStudent;
import com.liccioni.school.jpa.repository.JpaCourseRepository;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Transactional
public class DefaultCourseService implements CourseService {

    private final JpaCourseRepository repository;
    private final JpaStudentRepository studentRepository;
    private final Supplier<String> idGenerator;

    public DefaultCourseService(JpaCourseRepository repository, JpaStudentRepository studentRepository, Supplier<String> idGenerator) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Flux<Course> findAll() {
        return Flux.fromIterable(repository.findAll()).map(this::fromJpa);
    }

    @Override
    public Mono<Course> save(CreateCourseRequest request) {
        return Mono.fromCallable(() -> repository.save(
                        JpaCourse.builder()
                                .id(idGenerator.get())
                                .name(request.courseName())
                                .build()))
                .map(this::fromJpa);
    }

    @Override
    public Mono<Course> findById(String courseId) {
        return Mono.justOrEmpty(repository.findById(courseId)).map(this::fromJpa);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public Mono<Course> modify(String id, Course course) {
        return Mono.justOrEmpty(repository.findById(id))
                .map(jpaCourse -> repository.save(jpaCourse.toBuilder()
                        .name(course.name()).build()))
                .map(this::fromJpa);
    }

    @Override
    public Mono<Boolean> deleteById(String courseId) {
        return Mono.just(repository.deleteById(courseId)).map(r -> r > 0);
    }

    @Override
    public Flux<Student> findStudentsByCourseId(String id) {
        return Flux.fromIterable(studentRepository.findStudentsTakingCourse(id))
                .map(this::fromJpa);
    }

    @Override
    public Flux<Student> findStudentsNotTakingCourse(String id) {
        return Flux.fromIterable(studentRepository.findStudentsNotTakingCourse(id))
                .map(this::fromJpa);
    }

    private Student fromJpa(JpaStudent jpaStudent) {
        return new Student(jpaStudent.getId(), jpaStudent.getStudentId(), jpaStudent.getName(), jpaStudent.getLastName());
    }

    private Course fromJpa(JpaCourse jpaCourse) {
        return new Course(jpaCourse.getId(), jpaCourse.getName());
    }
}
