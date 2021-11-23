package com.liccioni.school.model;

import java.util.List;

public record StudentRegistrations(Student student, List<CourseRegistration> registrations) {
}
