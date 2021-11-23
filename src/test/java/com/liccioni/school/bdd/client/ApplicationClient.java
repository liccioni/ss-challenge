package com.liccioni.school.bdd.client;

import com.liccioni.school.bdd.steps.CreateStudentRegistrationRequest;
import com.liccioni.school.bdd.steps.ScoreRequest;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.RegistrationScores;
import com.liccioni.school.model.Student;
import com.liccioni.school.model.StudentRegistrations;

import java.util.List;
import java.util.Optional;

public interface ApplicationClient {

    void deleteAllCourses();

    List<Course> getAllCourses();

    Course createCourseWithName(String courseName);

    Course findCourseById(String id);

    Course modifyCourse(String oldName, String newName);

    boolean deleteCourse(String id);

    void deleteAllStudents();

    List<Student> getAllStudents();

    Student createStudent(Student request);

    Student findStudentById(String id);

    Student modifyStudent(Student newDetails);

    boolean deleteStudent(String id);

    StudentRegistrations createStudentRegistration(CreateStudentRegistrationRequest request);

    List<Student> getStudentsByCourseId(String id);

    List<Student> getStudentsNotInCourseByCourseId(String id);

    Optional<Student> getCachedStudent(String studentId);

    Optional<Course> getCachedCourse(String courseName);

    RegistrationScores submitScores(ScoreRequest scoreRequest);

    RegistrationScores getScores(ScoreRequest scoreRequest);
}
