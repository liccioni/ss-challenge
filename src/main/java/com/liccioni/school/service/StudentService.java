package com.liccioni.school.service;

import com.liccioni.school.http.student.CreateScoreRequest;
import com.liccioni.school.http.student.CreateStudentRequest;
import com.liccioni.school.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StudentService {

    void deleteAll();

    Flux<Student> findAll();

    Mono<Student> save(CreateStudentRequest request);

    Mono<Student> findById(String id);

    Mono<Student> modify(String id, Student newDetails);

    Mono<Boolean> deleteById(String id);

    Flux<Registration> register(String id, List<String> coursesId);

    Mono<StudentRegistrations> findRegistrations(String id);

    Flux<Score> submitScores(String registrationId, List<CreateScoreRequest> scores);

    Mono<RegistrationScores> findScores(String registrationId);
}
