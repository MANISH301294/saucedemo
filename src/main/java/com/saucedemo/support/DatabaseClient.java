package com.saucedemo.support;

import com.saucedemo.config.TestConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Config-driven JDBC utility. Inject credentials from a secret store in CI. */
public final class DatabaseClient implements AutoCloseable {
    private final HikariDataSource dataSource;

    public DatabaseClient() {
        if (TestConfig.dbUrl().isBlank()) {
            throw new IllegalStateException("DB_URL/dbUrl is required for database validation.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(TestConfig.dbUrl());
        config.setUsername(TestConfig.dbUser());
        config.setPassword(TestConfig.dbPassword());
        config.setMaximumPoolSize(2);
        config.setPoolName("ui-test-db-pool");
        dataSource = new HikariDataSource(config);
    }

    public List<Map<String, Object>> query(String sql, List<Object> parameters) {
        try (var connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }

            try (ResultSet results = statement.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                var metadata = results.getMetaData();

                while (results.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int column = 1; column <= metadata.getColumnCount(); column++) {
                        row.put(metadata.getColumnLabel(column), results.getObject(column));
                    }
                    rows.add(row);
                }

                return rows;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Database query failed", exception);
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
