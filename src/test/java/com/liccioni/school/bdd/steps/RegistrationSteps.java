package com.liccioni.school.bdd.steps;

import com.liccioni.school.bdd.client.ApplicationClient;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.Student;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistrationSteps {

    private final ApplicationClient applicationClient;

    public RegistrationSteps(ApplicationClient applicationClient) {
        this.applicationClient = applicationClient;
    }

    @When("students register to courses:")
    public void students_register_to_courses(List<CreateStudentRegistrationRequest> createStudentRegistrationRequests) {
        assertThat(createStudentRegistrationRequests.stream().map(applicationClient::createStudentRegistration))
                .hasSize(createStudentRegistrationRequests.size());
    }

    @Then("courses have students registered sorted by name:")
    public void courses_have_students_registered(List<CourseNameWithStudents> courseNameWithStudents) {
        courseNameWithStudents.forEach(c -> {
            Student[] expected = c.studentIds().stream()
                    .map(applicationClient::getCachedStudent)
                    .filter(Optional::isPresent).map(Optional::get)
                    .sorted(Comparator.comparing(Student::lastName).thenComparing(Student::name))
                    .toArray(Student[]::new);
            String courseId = applicationClient.getCachedCourse(c.courseName()).map(Course::id).orElse(null);
            List<Student> actual = applicationClient.getStudentsByCourseId(courseId);
            assertThat(actual).containsExactly(expected);
        });
    }

    @Then("students not registered in course:")
    public void students_not_registered_in_course(List<CourseNameWithStudents> courseNameWithStudents) {
        courseNameWithStudents.forEach(c -> {
            Student[] expected = c.studentIds().stream()
                    .map(applicationClient::getCachedStudent)
                    .filter(Optional::isPresent).map(Optional::get)
                    .toArray(Student[]::new);
            String courseId = applicationClient.getCachedCourse(c.courseName()).map(Course::id).orElse(null);
            List<Student> actual = applicationClient.getStudentsNotInCourseByCourseId(courseId);
            assertThat(actual).containsExactlyInAnyOrder(expected);
        });
    }

    @When("students register to courses an error occurs:")
    public void students_register_to_courses_an_error_occurs(List<CreateStudentRegistrationRequest> requests) {
        AssertionError assertionError = assertThrows(AssertionError.class, () -> requests.forEach(applicationClient::createStudentRegistration));
        assertThat(assertionError.getMessage()).isEqualTo("Status expected:<201 CREATED> but was:<409 CONFLICT>");
    }

    @DataTableType
    public CreateStudentRegistrationRequest registrationRequest(Map<String, String> map) {
        String id = applicationClient.getCachedStudent(map.get("studentId")).map(Student::id).orElse(null);
        return new CreateStudentRegistrationRequest(id,
                Arrays.stream(map.get("courseNames").split(","))
                        .map(applicationClient::getCachedCourse)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Course::id)
                        .toList());
    }

    @DataTableType
    public CourseNameWithStudents courseNameWithStudents(Map<String, String> map) {
        List<String> studentIds = Arrays.stream(Optional.ofNullable(map.get("studentIds")).orElse(",").split(",")).toList();
        return new CourseNameWithStudents(map.get("courseName"),
                studentIds);
    }
}
