package com.liccioni.school.http.student;

import java.util.List;

public record CreateRegistrationRequest(List<String> courseIds) {
}
