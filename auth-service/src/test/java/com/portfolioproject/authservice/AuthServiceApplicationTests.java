package com.portfolioproject.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// Starts the whole app on a test Postgres; with ddl-auto: validate this proves the entities match the migration.
@SpringBootTest
@Import(TestcontainersConfig.class)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
