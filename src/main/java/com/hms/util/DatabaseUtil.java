package com.hms.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.err.println("Sorry, unable to find config.properties");
                } else {
                    props.load(input);
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            
            // HikariCP recommended settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database connection pool.");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is null. Check configuration.");
        }
        return dataSource.getConnection();
    }
}
