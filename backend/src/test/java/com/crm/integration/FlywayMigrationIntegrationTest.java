package com.crm.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlywayMigrationIntegrationTest {

    @Test
    void completeMigrationChainBuildsExpectedPlans() throws Exception {
        String url = "jdbc:h2:mem:flyway;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        Flyway.configure()
                .dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .load()
                .migrate();

        try (var connection = DriverManager.getConnection(url, "sa", "");
             var statement = connection.prepareStatement(
                     "select count(*) from plans where name in ('STARTER','BUSINESS','ENTERPRISE','AGENCY')")) {
            try (var result = statement.executeQuery()) {
                result.next();
                assertEquals(4, result.getInt(1));
            }
        }
    }
}
