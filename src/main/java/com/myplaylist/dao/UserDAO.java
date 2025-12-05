package com.myplaylist.dao; // 1. Package disesuaikan

import com.myplaylist.db.Database; // Import koneksi database
import com.myplaylist.model.User; // Import model user

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class UserDAO {
    // Setup logger
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // Setup Konstanta
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_ROLE = "role";
    private static final String SELECT_ALL_USERS = 
        "SELECT " + COL_ID + ", " + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_ROLE + " FROM users";

    // Fitur Register
    public boolean addUser(User user) {
        // ID tidak perlu dimasukkan karena AUTO_INCREMENT
        String sql = "INSERT INTO users(" + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_ROLE + ") VALUES(?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal Register User", e);
            return false;
        }
    }

    // Fitur Login
    public User findUser(String username, String password) {
        String sql = SELECT_ALL_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 2. PERBAIKAN UTAMA: Ambil ID dari database!
                return new User(
                        rs.getInt(COL_ID), // Ambil kolom ID
                        rs.getString(COL_USERNAME),
                        rs.getString(COL_PASSWORD),
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fitur Cek Username (Biar gak dobel saat register)
    public User findUserByUsername(String username) {
        String sql = SELECT_ALL_USERS + " WHERE " + COL_USERNAME + " = ?";

        try (Connection conn = Database.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Ambil ID juga disini
                return new User(
                        rs.getInt("id"),
                        rs.getString(COL_USERNAME),
                        rs.getString(COL_PASSWORD),
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username", e);
        }
        return null;
    }
}