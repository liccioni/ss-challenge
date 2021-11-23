package com.liccioni.school.jpa.repository;

import com.liccioni.school.jpa.JpaStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaStudentRepository extends JpaRepository<JpaStudent, Long> {

    Optional<JpaStudent> findById(String id);

    Long deleteById(String id);

    @Query(value = "select r.student from JpaRegistration r where r.course.id = ?1 order by r.student.lastName, r.student.name")
    List<JpaStudent> findStudentsTakingCourse(String id);

    @Query(value = "from JpaStudent s where s not in (select r.student from JpaRegistration r where r.course.id = ?1)")
    List<JpaStudent> findStudentsNotTakingCourse(String id);
}
