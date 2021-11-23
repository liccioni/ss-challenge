package com.liccioni.school.config;

import com.liccioni.school.jpa.repository.JpaCourseRepository;
import com.liccioni.school.jpa.repository.JpaRegistrationRepository;
import com.liccioni.school.jpa.repository.JpaStudentRepository;
import com.liccioni.school.service.CourseService;
import com.liccioni.school.service.DefaultCourseService;
import com.liccioni.school.service.DefaultStudentService;
import com.liccioni.school.service.StudentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ApplicationConfig {

    @Bean
    public CourseService courseService(JpaCourseRepository repository,
                                       JpaStudentRepository studentRepository) {
        return new DefaultCourseService(repository, studentRepository, () -> UUID.randomUUID().toString());
    }

    @Bean
    public StudentService studentService(JpaStudentRepository repository,
                                         JpaRegistrationRepository registrationRepository,
                                         JpaCourseRepository courseRepository) {
        return new DefaultStudentService(
                repository,
                registrationRepository,
                courseRepository,
                () -> UUID.randomUUID().toString());
    }
}
