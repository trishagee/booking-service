package com.jetbrains.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;

import static java.lang.String.format;

public class MySQLContainerExtension implements BeforeAllCallback, AfterAllCallback {
    private MySQLContainer mySQLContainer;

    @Override
    public void beforeAll(ExtensionContext context) {
        String username = "user";
        String password = "app-password";
        String databaseName = "booking_service_db";

        mySQLContainer = new MySQLContainer("mysql:8.0.25")
                .withDatabaseName(databaseName)
                .withUsername(username)
                .withPassword(password);

        mySQLContainer.start();
        String jdbcUrl = format("jdbc:mysql://localhost:%d/%s", mySQLContainer.getFirstMappedPort(), databaseName);
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", username);
        System.setProperty("spring.datasource.password", password);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        mySQLContainer.stop();
    }
}
