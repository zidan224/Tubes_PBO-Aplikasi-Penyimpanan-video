package com.myplaylist.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // --- MySQL Configuration ---
    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    
    // PERBAIKAN 1: Nama Database disesuaikan dengan phpMyAdmin kamu
    private static final String MYSQL_DB_NAME = "watchlistdb"; 
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";

    private static final String DB_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_DB_NAME + "?serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
            throw new SQLException(e);
        }
        return DriverManager.getConnection(DB_URL, MYSQL_USER, MYSQL_PASSWORD);
    }

    public static void createTablesAndDummyData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")";
            stmt.execute(createUsersTable);

            // 2. Create videos table
            String createVideosTable = "CREATE TABLE IF NOT EXISTS videos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "creator VARCHAR(255)," + 
                    "category VARCHAR(255)," +
                    "year INT," +
                    "genre VARCHAR(100)," +
                    "duration DOUBLE" +
                    ")";
            stmt.execute(createVideosTable);

            // PERBAIKAN 2: Tambahkan pembuatan tabel Watchlist agar lengkap
            String createWatchlistTable = "CREATE TABLE IF NOT EXISTS watchlist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "video_id INT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'QUEUED'," +
                    "last_watch_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(createWatchlistTable);
            
            System.out.println("✅ Semua Tabel (Users, Videos, Watchlist) siap!");

        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown database")) {
                System.out.println("❌ Database '" + MYSQL_DB_NAME + "' belum dibuat di phpMyAdmin!");
            } else {
                e.printStackTrace();
            }
        }
    }
}