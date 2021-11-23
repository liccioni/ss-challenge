package com.liccioni.school.bdd;

import com.liccioni.school.bdd.config.PostgresTestContainerInitializer;
import com.liccioni.school.bdd.config.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@Import(TestConfig.class)
@AutoConfigureWebTestClient
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
public class CucumberSpringConfiguration {
}
