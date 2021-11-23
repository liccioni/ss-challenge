package com.liccioni.school.bdd.steps;

import java.util.List;

public record CreateStudentRegistrationRequest(String studentId, List<String> courseIds) {
}
