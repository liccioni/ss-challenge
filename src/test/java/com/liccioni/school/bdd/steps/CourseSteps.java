package com.liccioni.school.bdd.steps;

import com.liccioni.school.bdd.client.ApplicationClient;
import com.liccioni.school.model.Course;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseSteps {

    private final ApplicationClient applicationClient;

    public CourseSteps(ApplicationClient applicationClient) {
        this.applicationClient = applicationClient;
    }

    @After
    @Before
    public void cleanUp() {
        applicationClient.deleteAllCourses();
    }

    @Given("there are no courses")
    public void no_courses_are_created() {
        List<Course> allCourses = applicationClient.getAllCourses();
        assertThat(allCourses).isEmpty();
    }

    @When("course with name {string} is created")
    public void course_with_name_is_created(String courseName) {
        Course createdCourse = applicationClient.createCourseWithName(courseName);
        assertThat(createdCourse.id()).isNotBlank();
        assertThat(createdCourse.name()).isEqualTo(courseName);
    }

    @Then("course with name {string} exists")
    public void course_with_name_exists(String courseName) {
        String id = applicationClient.getCachedCourse(courseName).map(Course::id).orElse(null);
        Course foundCourse = applicationClient.findCourseById(id);
        assertThat(foundCourse.id()).isNotBlank();
        assertThat(foundCourse.name()).isEqualToIgnoringCase(courseName);
    }

    @When("course with name {string} is modified to {string}")
    public void course_with_name_is_modified_to(String oldName, String newName) {
        Course modifiedCourse = applicationClient.modifyCourse(oldName, newName);
        assertThat(modifiedCourse.name()).isEqualToIgnoringCase(newName);
    }

    @When("course with name {string} is deleted")
    public void course_with_name_is_deleted(String courseName) {
        String id = applicationClient.getCachedCourse(courseName).map(Course::id).orElse(null);
        assertTrue(applicationClient.deleteCourse(id));
    }

    @Then("course with name {string} does not exists")
    public void course_with_name_does_not_exists(String courseName) {
        String id = applicationClient.getCachedCourse(courseName).map(Course::id).orElse(null);
        AssertionError assertionError = assertThrows(AssertionError.class, () -> applicationClient.findCourseById(id));
        assertThat(assertionError.getMessage()).isEqualTo("Status expected:<200 OK> but was:<404 NOT_FOUND>");
    }

    @When("course with name {string} is created an error occurs")
    public void course_with_name_is_created_an_error_occurs(String courseName) {
        AssertionError assertionError = assertThrows(AssertionError.class, () -> applicationClient.createCourseWithName(courseName));
        assertThat(assertionError.getMessage()).isEqualTo("Status expected:<201 CREATED> but was:<409 CONFLICT>");
    }

    @Given("following courses are created:")
    public void following_courses_are_created(List<String> courseNames) {
        courseNames.forEach(applicationClient::createCourseWithName);
    }

    @Then("following courses exist:")
    public void following_courses_exist(List<String> courseNames) {
        List<String> actualNames = applicationClient.getAllCourses().stream().map(Course::name).toList();
        assertThat(actualNames).usingElementComparator(String::compareToIgnoreCase)
                .containsExactlyInAnyOrder(courseNames.toArray(String[]::new));
    }
}
