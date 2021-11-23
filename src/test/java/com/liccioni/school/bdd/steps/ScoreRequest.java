package com.liccioni.school.bdd.steps;

import com.liccioni.school.http.student.CreateScoreRequest;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;

import java.util.List;

public record ScoreRequest(Student student, Course course, List<CreateScoreRequest> score) {
}
