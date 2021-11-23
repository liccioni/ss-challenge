package com.liccioni.school.bdd.steps;

import com.liccioni.school.bdd.client.ApplicationClient;
import com.liccioni.school.model.Student;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentSteps {

    private final ApplicationClient applicationClient;

    public StudentSteps(ApplicationClient applicationClient) {
        this.applicationClient = applicationClient;
    }

    @After
    @Before
    public void cleanUp() {
        applicationClient.deleteAllStudents();
    }

    @Given("there are no students")
    public void there_are_no_students() {
        List<Student> allStudents = applicationClient.getAllStudents();
        assertThat(allStudents).isEmpty();
    }

    @When("student with details is created:")
    public void student_with_details_is_created(Student student) {
        Student actual = applicationClient.createStudent(student);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
    }

    @Then("student with id {string} exists with details:")
    public void student_with_id_exists_with_details(String studentId, Student expected) {
        String id = applicationClient.getCachedStudent(studentId).map(Student::id).orElse(null);
        Student actual = applicationClient.findStudentById(id);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @When("student with id {string} details are modified to:")
    public void student_with_id_details_are_modified_to(String studentId, Student expected) {
        Student actual = applicationClient.modifyStudent(expected);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @When("student with id {string} is deleted")
    public void student_with_id_is_deleted(String studentId) {
        String id = applicationClient.getCachedStudent(studentId).map(Student::id).orElse(null);
        assertThat(applicationClient.deleteStudent(id)).isTrue();
    }

    @Then("student with id {string} does not exists")
    public void student_with_id_does_not_exists(String studentId) {
        AssertionError assertionError = assertThrows(AssertionError.class, () -> applicationClient.findStudentById(studentId));
        assertThat(assertionError.getMessage()).isEqualTo("Status expected:<200 OK> but was:<404 NOT_FOUND>");
    }

    @When("student with details is created an error occurs:")
    public void student_with_details_is_created_an_error_occurs(Student student) {
        AssertionError assertionError = assertThrows(AssertionError.class, () -> applicationClient.createStudent(student));
        assertThat(assertionError.getMessage()).isEqualTo("Status expected:<201 CREATED> but was:<409 CONFLICT>");
    }

    @Given("following students are created:")
    public void following_students_are_created(List<Student> students) {
        students.forEach(applicationClient::createStudent);
    }

    @Then("following students exist:")
    public void following_students_exist(List<Student> students) {
        List<Student> actualAll = applicationClient.getAllStudents();
        assertThat(actualAll)
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder()
                        .withComparatorForFields((Comparator<String>) String::compareToIgnoreCase, "name", "lastName")
                        .withIgnoreAllExpectedNullFields(true)
                        .build())
                .containsExactlyInAnyOrder(students.toArray(Student[]::new));
    }

    @DataTableType
    public Student student(Map<String, String> map) {
        String id = applicationClient.getCachedStudent(map.get("studentId")).map(Student::id).orElse(null);
        return new Student(id, map.get("studentId"), map.get("firstName"), map.get("lastName"));
    }
}
