package com.liccioni.school.bdd.steps;

import java.util.List;

public record CourseNameWithStudents(String courseName, List<String> studentIds) {
}
