package com.liccioni.school.http.student;

import com.liccioni.school.model.RegistrationScores;
import com.liccioni.school.model.Student;
import com.liccioni.school.model.StudentRegistrations;
import com.liccioni.school.service.StudentService;
import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @DeleteMapping
    public Publisher<ResponseEntity<Void>> deleteAll() {
        return Mono.fromRunnable(studentService::deleteAll)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping
    public Publisher<Student> findAll() {
        return studentService.findAll();
    }

    @PostMapping
    public Publisher<ResponseEntity<Void>> create(@RequestBody Mono<CreateStudentRequest> request,
                                                  UriComponentsBuilder componentsBuilder) {
        UriComponentsBuilder uriComponents = componentsBuilder.path("/students/{id}");
        return request.flatMap(studentService::save)
                .map(c -> ResponseEntity.created(uriComponents.buildAndExpand(c.id()).toUri()).build());
    }

    @GetMapping("/{id}")
    public Publisher<ResponseEntity<Student>> findById(@PathVariable String id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Publisher<ResponseEntity<Student>> modify(@PathVariable String id, @RequestBody Mono<Student> newDetails) {
        return newDetails.flatMap(newDetails1 -> studentService.modify(id, newDetails1))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Publisher<ResponseEntity<Object>> delete(@PathVariable String id) {
        return studentService.deleteById(id)
                .filter(result -> result)
                .map(p -> ResponseEntity.noContent().build())
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{id}/registrations")
    public Publisher<ResponseEntity<Void>> createRegistration(@PathVariable String id,
                                                              @RequestBody Mono<CreateRegistrationRequest> request,
                                                              UriComponentsBuilder componentsBuilder) {
        UriComponentsBuilder uriComponents = componentsBuilder.path("/students/{id}/registrations");
        return request.map(CreateRegistrationRequest::courseIds)
                .flatMapMany(coursesId -> studentService.register(id, coursesId))
                .reduce(ResponseEntity.created(uriComponents.buildAndExpand(id).toUri()), (bodyBuilder, studentRegistration) -> bodyBuilder)
                .map(ResponseEntity.HeadersBuilder::build);
    }

    @GetMapping("/{id}/registrations")
    public Publisher<ResponseEntity<StudentRegistrations>> createRegistration(@PathVariable String id) {
        return studentService.findRegistrations(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/registrations/{registrationId}/scores")
    public Publisher<ResponseEntity<Void>> submitScores(@PathVariable String id,
                                                        @PathVariable String registrationId,
                                                        @RequestBody Mono<CreateScoresRequest> request,
                                                        UriComponentsBuilder componentsBuilder) {
        UriComponentsBuilder uriComponents = componentsBuilder.path("/students/{id}/registrations/{registrationId}/scores");
        return request.map(CreateScoresRequest::scores)
                .flatMapMany(scores -> studentService.submitScores(registrationId, scores))
                .reduce(ResponseEntity.created(uriComponents.buildAndExpand(id, registrationId).toUri()), (builder, score) -> builder)
                .map(ResponseEntity.HeadersBuilder::build);
    }

    @GetMapping("/{id}/registrations/{registrationId}/scores")
    public Publisher<ResponseEntity<RegistrationScores>> getScores(@PathVariable String id,
                                                                   @PathVariable String registrationId) {
        return studentService.findScores(registrationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
