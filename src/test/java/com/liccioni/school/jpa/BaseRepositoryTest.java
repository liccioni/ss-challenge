package com.liccioni.school.jpa;

import com.liccioni.school.bdd.config.PostgresTestContainerInitializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.format_sql=true"})
public abstract class BaseRepositoryTest {
}
