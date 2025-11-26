package com.myplaylist.db; // 1. Package disesuaikan

import com.myplaylist.db.Database; // Import koneksi database
import com.myplaylist.model.User;  // Import model user

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Fitur Register
    public boolean addUser(User user) {
        // ID tidak perlu dimasukkan karena AUTO_INCREMENT
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); 
            pstmt.setString(3, user.getRole());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal Register: " + e.getMessage());
            return false;
        }
    }

    // Fitur Login
    public User findUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 2. PERBAIKAN UTAMA: Ambil ID dari database!
                return new User(
                    rs.getInt("id"),          // Ambil kolom ID
                    rs.getString("username"), 
                    rs.getString("password"), 
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fitur Cek Username (Biar gak dobel saat register)
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Ambil ID juga disini
                return new User(
                    rs.getInt("id"), 
                    rs.getString("username"), 
                    rs.getString("password"), 
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}