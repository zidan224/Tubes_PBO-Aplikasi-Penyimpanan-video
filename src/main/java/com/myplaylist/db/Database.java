package com.myplaylist.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // 1. Static variable untuk menyimpan satu-satunya instance
    private static Database instance;

    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_DB_NAME = "watchlistdb";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";
    private static final String DB_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_DB_NAME
            + "?serverTimezone=UTC";

    // 2. Private Constructor agar tidak bisa di-new sembarangan
    private Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 3. Public Static Method untuk mengakses instance (Global Access Point)
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Method koneksi sekarang bukan static lagi, tapi instance method
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, MYSQL_USER, MYSQL_PASSWORD);
    }

    // Utility untuk setup awal (bisa tetap static atau dibuat instance juga, kita
    // buat static helper saja)
    public static void createTablesAndDummyData() {
        // ... (Kode create table tetap sama, tapi panggil getConnection via instance)
        try (Connection conn = getInstance().getConnection(); // Panggil via Singleton
                Statement stmt = conn.createStatement()) {

            // ... (Salin isi create tables dari file lama di sini)
            // User table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL)";
            stmt.execute(createUsersTable);

            // Video table
            String createVideosTable = "CREATE TABLE IF NOT EXISTS videos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "creator VARCHAR(255)," +
                    "category VARCHAR(255)," +
                    "year INT," +
                    "genre VARCHAR(100)," +
                    "duration DOUBLE)";
            stmt.execute(createVideosTable);

            // Watchlist table
            String createWatchlistTable = "CREATE TABLE IF NOT EXISTS watchlist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "video_id INT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'QUEUED'," +
                    "last_watch_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE)";
            stmt.execute(createWatchlistTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}