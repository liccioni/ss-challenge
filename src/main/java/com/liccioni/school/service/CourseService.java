package com.liccioni.school.service;

import com.liccioni.school.http.course.CreateCourseRequest;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseService {

    Flux<Course> findAll();

    Mono<Course> save(CreateCourseRequest request);

    Mono<Course> findById(String courseId);

    void deleteAll();

    Mono<Course> modify(String id, Course course);

    Mono<Boolean> deleteById(String courseId);

    Flux<Student> findStudentsByCourseId(String id);

    Flux<Student> findStudentsNotTakingCourse(String id);
}
