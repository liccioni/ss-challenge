package com.liccioni.school.http.course;

import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import com.liccioni.school.service.CourseService;
import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Publisher<Course> getAllCourses() {
        return courseService.findAll();
    }

    @PostMapping
    public Publisher<ResponseEntity<Void>> createCourse(@RequestBody Mono<CreateCourseRequest> request,
                                                        UriComponentsBuilder componentsBuilder) {
        UriComponentsBuilder uriComponents = componentsBuilder.path("/courses/{id}");
        return request.flatMap(courseService::save)
                .map(c -> ResponseEntity.created(uriComponents.buildAndExpand(c.id()).toUri()).build());
    }

    @GetMapping("/{id}")
    public Publisher<ResponseEntity<Course>> findById(@PathVariable String id) {
        return courseService.findById(id).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Publisher<ResponseEntity<Void>> deleteAll() {
        return Mono.fromRunnable(courseService::deleteAll)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}")
    public Publisher<ResponseEntity<Course>> modifyCourse(@PathVariable String id, @RequestBody Mono<Course> request) {
        return request.flatMap(course -> courseService.modify(id, course)).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Publisher<ResponseEntity<Object>> deleteById(@PathVariable String id) {
        return courseService.deleteById(id)
                .filter(result -> result)
                .map(p -> ResponseEntity.noContent().build())
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{id}/students")
    public Publisher<Student> getStudentsInCourse(@PathVariable String id,
                                                  @RequestParam(name = "not-in-course", required = false, defaultValue = "false") boolean notInCourse) {
        if (notInCourse) {
            return courseService.findStudentsNotTakingCourse(id);
        } else {
            return courseService.findStudentsByCourseId(id);
        }
    }
}
