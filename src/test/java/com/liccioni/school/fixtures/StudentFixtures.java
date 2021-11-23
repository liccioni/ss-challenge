package com.liccioni.school.fixtures;

import com.liccioni.school.jpa.JpaStudent;
import com.liccioni.school.model.Student;
import org.assertj.core.util.Arrays;

import java.util.List;

public class StudentFixtures {

    public static Student[] all() {
        return Arrays.array(jerry(), george(), elaine(), kramer(), newman());
    }

    public static List<JpaStudent> allJpa() {
        return java.util.Arrays.stream(all())
                .map(StudentFixtures::toJpa).toList();
    }

    public static Student jerry() {
        return new Student("1", "S001", "Jerry", "Seinfeld");
    }

    public static Student george() {
        return new Student("2", "S002", "George", "Costanza");
    }

    public static Student elaine() {
        return new Student("3", "S003", "Elaine", "Benes");
    }

    public static Student kramer() {
        return new Student("4", "S004", "Cosmo", "Kramer");
    }

    public static Student newman() {
        return new Student("5", "S005", "Hello", "Newman");
    }

    public static JpaStudent jpaJerry() {
        return toJpa(jerry());
    }

    public static JpaStudent jpaGeorge() {
        return toJpa(george());
    }

    public static JpaStudent jpaElaine() {
        return toJpa(elaine());
    }

    public static JpaStudent jpaNewman() {
        return toJpa(newman());
    }

    public static JpaStudent jpaKramer() {
        return toJpa(kramer());
    }

    private static JpaStudent toJpa(Student s) {
        return JpaStudent.builder()
                .pk(Long.parseLong(s.id()))
                .id(s.id())
                .studentId(s.studentId())
                .name(s.name())
                .lastName(s.lastName())
                .build();
    }
}
