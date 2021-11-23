package com.liccioni.school.jpa.repository;

import com.liccioni.school.jpa.JpaRegistration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRegistrationRepository extends JpaRepository<JpaRegistration, Long> {

    @EntityGraph(value = "registration.graph")
    List<JpaRegistration> findByStudentId(String id);

    @EntityGraph(value = "registration.graph")
    Optional<JpaRegistration> findById(String id);
}
