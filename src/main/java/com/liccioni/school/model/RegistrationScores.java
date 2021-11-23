package com.liccioni.school.model;

import java.util.List;

public record RegistrationScores(Student student, Course course, List<Score> scores) {
}
