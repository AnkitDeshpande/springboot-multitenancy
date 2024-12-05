package com.springsecurity.springsecurity.service.impl;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class FlywayService {

    @Value("${spring.datasource.url}")
    private String masterDbUrl;

    @Value("${spring.datasource.username}")
    private String masterDbUsername;

    @Value("${spring.datasource.password}")
    private String masterDbPassword;

    public void createTenantDatabase(String tenantDbName) {
        /* Create a new database for the tenant */
        DataSource masterDataSource = new DriverManagerDataSource(masterDbUrl, masterDbUsername, masterDbPassword);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        jdbcTemplate.execute("CREATE DATABASE client_" + tenantDbName);

        /* Configure Flyway for the new tenant database */
        String tenantDbUrl = "jdbc:mysql://localhost:3306/client_" + tenantDbName;
        Flyway flyway = Flyway.configure()
                .dataSource(tenantDbUrl, masterDbUsername, masterDbPassword)
                .locations("classpath:db/patches")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }
}
