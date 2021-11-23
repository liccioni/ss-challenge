package com.liccioni.school.fixtures;

import com.liccioni.school.jpa.JpaCourse;
import com.liccioni.school.model.Course;

public class CourseFixtures {
    public static Course math() {
        return new Course("1", "math");
    }

    public static Course history() {
        return new Course("2", "history");
    }

    public static Course science() {
        return new Course("3", "science");
    }

    public static Course english() {
        return new Course("4", "english");
    }

    public static JpaCourse jpaMath() {
        return toJpa(math());
    }

    public static JpaCourse jpaHistory() {
        return toJpa(history());
    }

    public static JpaCourse jpaScience() {
        return toJpa(science());
    }

    public static JpaCourse jpaEnglish() {
        return toJpa(english());
    }

    private static JpaCourse toJpa(Course course) {
        return JpaCourse.builder()
                .pk(Long.parseLong(course.id()))
                .id(course.id())
                .name(course.name())
                .build();
    }
}
