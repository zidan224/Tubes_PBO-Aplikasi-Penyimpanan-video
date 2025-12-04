package com.myplaylist.dao;

import com.myplaylist.db.Database;
import com.myplaylist.model.Video;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    public List<Video> getWatchlistByUserId(int userId) {
        List<Video> userVideos = new ArrayList<>();
        String sql = "SELECT v.* FROM videos v " +
                "JOIN watchlist w ON v.id = w.video_id " +
                "WHERE w.user_id = ?";

        try (Connection conn = Database.getInstance().getConnection();
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
                        rs.getDouble("duration"),
                        rs.getString("thumbnail_path") 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userVideos;
    }

    public boolean addVideoToWatchlist(int userId, int videoId) {
        if (isDuplicate(userId, videoId)) return false;

        String sql = "INSERT INTO watchlist (user_id, video_id, status) VALUES (?, ?, 'QUEUED')";
        try (Connection conn = Database.getInstance().getConnection();
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

    public boolean removeVideoFromWatchlist(int userId, int videoId) {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND video_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
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

    private boolean isDuplicate(int userId, int videoId) {
        String sql = "SELECT id FROM watchlist WHERE user_id = ? AND video_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, videoId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}