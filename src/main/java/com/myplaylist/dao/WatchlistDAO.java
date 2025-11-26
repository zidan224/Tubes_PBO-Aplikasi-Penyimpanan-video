package com.myplaylist.dao;

import com.myplaylist.db.Database;
import com.myplaylist.model.Video;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    // 1. AMBIL VIDEO MILIK USER TERTENTU (JOIN TABLE)
    // Ini query paling penting: Menggabungkan tabel videos dan watchlist
    public List<Video> getWatchlistByUserId(int userId) {
        List<Video> userVideos = new ArrayList<>();
        String sql = "SELECT v.* FROM videos v " +
                     "JOIN watchlist w ON v.id = w.video_id " +
                     "WHERE w.user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                userVideos.add(new Video(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("creator"),
                    rs.getString("category"),
                    rs.getInt("year"),
                    rs.getString("genre"),
                    rs.getDouble("duration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userVideos;
    }

    // 2. TAMBAH VIDEO KE WATCHLIST
    public boolean addVideoToWatchlist(int userId, int videoId) {
        // Cek dulu biar gak duplikat (Opsional tapi bagus)
        if (isDuplicate(userId, videoId)) return false;

        String sql = "INSERT INTO watchlist (user_id, video_id, status) VALUES (?, ?, 'QUEUED')";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. HAPUS VIDEO DARI WATCHLIST
    public boolean removeVideoFromWatchlist(int userId, int videoId) {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND video_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cek Duplikat
    private boolean isDuplicate(int userId, int videoId) {
        String sql = "SELECT id FROM watchlist WHERE user_id = ? AND video_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True kalau sudah ada
        } catch (SQLException e) {
            return false;
        }
    }
}