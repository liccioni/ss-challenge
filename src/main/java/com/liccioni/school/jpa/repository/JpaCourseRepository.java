package com.liccioni.school.jpa.repository;

import com.liccioni.school.jpa.JpaCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaCourseRepository extends JpaRepository<JpaCourse, Long> {

    Optional<JpaCourse> findById(String courseId);

    Integer deleteById(String courseId);

    List<JpaCourse> findByIdIn(List<String> coursesId);
}
