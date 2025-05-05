package database;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mariadb://localhost:3306/healthcentre";
    private static final String USER = "root";
    private static final String PASSWORD = "Amoury123";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}