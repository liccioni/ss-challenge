package com.liccioni.school.bdd.steps;

import com.liccioni.school.bdd.client.ApplicationClient;
import com.liccioni.school.http.student.CreateScoreRequest;
import com.liccioni.school.model.Course;
import com.liccioni.school.model.RegistrationScores;
import com.liccioni.school.model.Score;
import com.liccioni.school.model.Student;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScoreSteps {

    private final ApplicationClient client;

    public ScoreSteps(ApplicationClient client) {
        this.client = client;
    }

    @When("Students are assigned scores:")
    public void students_are_assigned_scores(List<ScoreRequest> requestList) {
        requestList.forEach(client::submitScores);
    }

    @Then("scores match:")
    public void scores_match(List<ScoreRequest> requestList) {
        requestList.forEach(scoreRequest -> {
            RegistrationScores actual = client.getScores(scoreRequest);
            RegistrationScores expected = toRegistrationScores(scoreRequest);
            assertThat(actual)
                    .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                            .withIgnoreAllExpectedNullFields(true)
                            .withIgnoreCollectionOrder(true)
                            .build())
                    .isEqualTo(expected);
        });
    }

    @DataTableType
    public ScoreRequest scoreRequest(Map<String, String> map) {
        Course course = client.getCachedCourse(map.get("courseName")).orElseThrow();
        Student student = client.getCachedStudent(map.get("studentId")).orElseThrow();
        List<CreateScoreRequest> scores = Arrays.stream(map.get("scores").split(","))
                .map(nameAndScore -> nameAndScore.split(":"))
                .map(chunks -> new CreateScoreRequest(chunks[0], Double.parseDouble(chunks[1]))).toList();
        return new ScoreRequest(student, course, scores);
    }

    private RegistrationScores toRegistrationScores(ScoreRequest scoreRequest) {
        List<Score> scores = scoreRequest.score().stream()
                .map(score -> new Score(null, score.name(), score.score())).toList();
        return new RegistrationScores(
                scoreRequest.student(),
                scoreRequest.course(),
                scores);
    }
}
