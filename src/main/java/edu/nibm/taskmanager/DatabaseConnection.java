package edu.nibm.taskmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USER = "root";
    private static final String PASSWORD = "Dsov@0605";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the MySQL server successfully.");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return conn;
    }
}
