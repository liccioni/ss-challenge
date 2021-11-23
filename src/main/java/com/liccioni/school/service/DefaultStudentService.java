package com.liccioni.school.service;

import com.liccioni.school.http.student.CreateScoreRequest;
import com.liccioni.school.http.student.CreateStudentRequest;
import com.liccioni.school.jpa.JpaRegistration;
import com.liccioni.school.jpa.JpaScore;
import com.liccioni.school.jpa.JpaStudent;
import com.liccioni.school.jpa.repository.JpaCourseRepository;
import com.liccioni.school.jpa.repository.JpaRegistrationRepository;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.model.*;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Transactional
public class DefaultStudentService implements StudentService {

    private final JpaStudentRepository repository;
    private final Supplier<String> idGenerator;
    private final JpaRegistrationRepository registrationRepository;
    private final JpaCourseRepository courseRepository;

    public DefaultStudentService(JpaStudentRepository repository,
                                 JpaRegistrationRepository registrationRepository,
                                 JpaCourseRepository courseRepository,
                                 Supplier<String> idGenerator) {
        this.repository = repository;
        this.courseRepository = courseRepository;
        this.idGenerator = idGenerator;
        this.registrationRepository = registrationRepository;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public Flux<Student> findAll() {
        return Flux.fromIterable(repository.findAll())
                .map(this::studentFromJpa);
    }

    @Override
    public Mono<Student> save(CreateStudentRequest request) {
        return Mono.fromCallable(() -> repository.save(toJpa(request))).map(this::studentFromJpa);
    }

    @Override
    public Mono<Student> findById(String id) {
        return Mono.justOrEmpty(repository.findById(id))
                .map(this::studentFromJpa);
    }

    @Override
    public Mono<Student> modify(String id, Student newDetails) {
        return Mono.justOrEmpty(repository.findById(id))
                .map(jpaStudent -> repository.save(jpaStudent.toBuilder()
                        .name(newDetails.name())
                        .lastName(newDetails.lastName())
                        .build()))
                .map(this::studentFromJpa);
    }

    @Override
    public Mono<Boolean> deleteById(String id) {
        return Mono.just(repository.deleteById(id)).map(r -> r > 0);
    }

    @Override
    public Flux<Registration> register(String id, List<String> coursesId) {

        return Mono.justOrEmpty(repository.findById(id))
                .flatMapMany(jpaStudent -> Flux.fromIterable(courseRepository.findByIdIn(coursesId)).map(c -> Tuples.of(jpaStudent, c)))
                .map(t -> registrationRepository.save(JpaRegistration.builder().id(idGenerator.get()).student(t.getT1()).course(t.getT2()).build()))
                .map(this::registrationFromJpa);
    }

    @Override
    public Mono<StudentRegistrations> findRegistrations(String id) {
        return Flux.fromIterable(registrationRepository.findByStudentId(id))
                .map(this::registrationFromJpa)
                .reduce(Tuples.of(new AtomicReference<>(), new ArrayList<>()), this::reduce)
                .map(objects -> new StudentRegistrations(objects.getT1().get(), objects.getT2()));
    }

    @Override
    public Flux<Score> submitScores(String registrationId, List<CreateScoreRequest> scores) {
        return Mono.justOrEmpty(registrationRepository.findById(registrationId))
                .map(jpaRegistration -> {
                    scores.stream().map(score -> toJpaScore(jpaRegistration, score)).forEach(jpaRegistration::addScore);
                    return registrationRepository.save(jpaRegistration);
                }).flatMapMany(jpaRegistration -> Flux.fromIterable(jpaRegistration.getScores()))
                .map(this::fromJpaScore);
    }

    @Override
    public Mono<RegistrationScores> findScores(String registrationId) {
        return Mono.justOrEmpty(registrationRepository.findById(registrationId))
                .map(this::fromJpaToRegistrationScores);
    }

    private Tuple2<AtomicReference<Student>, ArrayList<CourseRegistration>> reduce(
            Tuple2<AtomicReference<Student>,
                    ArrayList<CourseRegistration>> studentRegistrations, Registration registration) {
        studentRegistrations.getT1().set(registration.student());
        studentRegistrations.getT2().add(new CourseRegistration(registration.id(), registration.course()));
        return studentRegistrations;
    }

    private Course courseFromJpa(JpaRegistration jpa) {
        return new Course(jpa.getCourse().getId(), jpa.getCourse().getName());
    }

    private Student studentFromJpa(JpaStudent jpaStudent) {
        return new Student(jpaStudent.getId(), jpaStudent.getStudentId(), jpaStudent.getName(), jpaStudent.getLastName());
    }

    private Registration registrationFromJpa(JpaRegistration jpa) {
        return new Registration(jpa.getId(), studentFromJpa(jpa.getStudent()), courseFromJpa(jpa));
    }

    private JpaStudent toJpa(CreateStudentRequest request) {
        return JpaStudent.builder()
                .id(idGenerator.get())
                .studentId(request.studentId())
                .name(request.name())
                .lastName(request.lastName())
                .build();
    }

    private JpaScore toJpaScore(JpaRegistration jpaRegistration, CreateScoreRequest score) {
        return JpaScore.builder()
                .id(idGenerator.get())
                .name(score.name())
                .score(score.score())
                .registration(jpaRegistration)
                .build();
    }

    private Score fromJpaScore(JpaScore jpaScore) {
        return new Score(jpaScore.getId(), jpaScore.getName(), jpaScore.getScore());
    }

    private RegistrationScores fromJpaToRegistrationScores(JpaRegistration jpa) {
        return new RegistrationScores(studentFromJpa(jpa.getStudent()), courseFromJpa(jpa),
                jpa.getScores().stream().map(this::fromJpaScore).toList());
    }
}
