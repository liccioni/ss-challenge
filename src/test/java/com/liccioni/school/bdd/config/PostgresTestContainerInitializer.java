package com.liccioni.school.bdd.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("school-db")
            .withUsername("postgres")
            .withPassword("test")
            .withExposedPorts(5432);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        postgres.start();
        TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
