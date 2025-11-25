package com.myplaylist.db;

import com.myplaylist.model.Video;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO {

    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();
        String sql = "SELECT * FROM videos";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getInt("year"),
                        rs.getString("genre"),
                        rs.getDouble("duration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public boolean addVideo(Video video) {
        String sql = "INSERT INTO videos(title, artist, album, year, genre, duration) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getTitle());
            pstmt.setString(2, video.getArtist());
            pstmt.setString(3, video.getAlbum());
            pstmt.setInt(4, video.getYear());
            pstmt.setString(5, video.getGenre());
            pstmt.setDouble(6, video.getDuration());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVideo(Video video) {
        String sql = "UPDATE videos SET title = ?, artist = ?, album = ?, year = ?, genre = ?, duration = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getTitle());
            pstmt.setString(2, video.getArtist());
            pstmt.setString(3, video.getAlbum());
            pstmt.setInt(4, video.getYear());
            pstmt.setString(5, video.getGenre());
            pstmt.setDouble(6, video.getDuration());
            pstmt.setInt(7, video.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVideo(int id) {
        String sql = "DELETE FROM videos WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Video getVideoById(int id) {
        String sql = "SELECT * FROM videos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getInt("year"),
                        rs.getString("genre"),
                        rs.getDouble("duration")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
