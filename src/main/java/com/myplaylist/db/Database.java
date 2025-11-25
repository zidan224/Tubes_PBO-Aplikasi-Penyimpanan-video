package com.myplaylist.db;

import com.myplaylist.auth.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // --- MySQL Configuration ---
    // PLEASE REPLACE these placeholders with your actual MySQL server details.
    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_DB_NAME = "playlistdb";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    private static final String DB_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_DB_NAME + "?serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {
        /*
         * --- IMPORTANT ---
         * Make sure you have the MySQL Connector/J driver in your project's classpath.
         * You can download it from the official MySQL website.
         * If using a build tool like Maven or Gradle, add it as a dependency.
         */
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add it to your classpath.");
            e.printStackTrace();
            throw new SQLException(e);
        }
        return DriverManager.getConnection(DB_URL, MYSQL_USER, MYSQL_PASSWORD);
    }

    public static void createTablesAndDummyData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")";
            stmt.execute(createUsersTable);

            // Create songs table (if it doesn't exist)
            String createSongsTable = "CREATE TABLE IF NOT EXISTS songs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "artist VARCHAR(255) NOT NULL," +
                    "album VARCHAR(255)," +
                    "year INT," +
                    "genre VARCHAR(100)," +
                    "duration DOUBLE" +
                    ")";
            stmt.execute(createSongsTable);

            // Insert dummy data
            insertDummyData();

        } catch (SQLException e) {
            // If the database doesn't exist, an exception is thrown.
            // We can try to create it.
            if (e.getMessage().contains("Unknown database")) {
                System.out.println("Database not found. Please create the database '" + MYSQL_DB_NAME + "' on your MySQL server.");
            } else {
                e.printStackTrace();
            }
        }
    }

    private static void insertDummyData() {
        UserDAO userDAO = new UserDAO();
        // Add admin user if not exists
        if (userDAO.findUserByUsername("admin") == null) {
            userDAO.addUser(new User("admin", "password", "admin"));
            System.out.println("Dummy admin user created.");
        }
        // Add normal user if not exists
        if (userDAO.findUserByUsername("user") == null) {
            userDAO.addUser(new User("user", "password", "user"));
            System.out.println("Dummy regular user created.");
        }
    }
}