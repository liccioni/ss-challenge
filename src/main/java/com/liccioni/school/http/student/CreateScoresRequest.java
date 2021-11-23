package com.liccioni.school.http.student;

import java.util.List;

public record CreateScoresRequest(List<CreateScoreRequest> scores) {
}

