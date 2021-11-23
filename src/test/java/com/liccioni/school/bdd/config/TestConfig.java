package com.liccioni.school.bdd.config;

import com.liccioni.school.bdd.client.ApplicationClient;
import com.liccioni.school.bdd.client.ApplicationWebClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestConfiguration
public class TestConfig {

    @Bean
    public ApplicationClient applicationClient(WebTestClient webTestClient) {
        return new ApplicationWebClient(webTestClient);
    }
}
