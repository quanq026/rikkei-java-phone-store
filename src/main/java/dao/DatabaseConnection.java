package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/jb_phonestore";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "admin";

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        String password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
}
