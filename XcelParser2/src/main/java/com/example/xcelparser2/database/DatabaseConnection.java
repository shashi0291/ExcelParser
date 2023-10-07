package com.example.xcelparser2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:sort_plan_database.db";
    private Connection connection;

    public DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_URL);

            // Example: Create a table
             Statement statement = connection.createStatement();
             String createTableSQL = "CREATE TABLE IF NOT EXISTS sort_plan_table (sortStartTime DATE, employeeId INTEGER)";
             statement.executeUpdate(createTableSQL);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isDatabaseOpen() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

