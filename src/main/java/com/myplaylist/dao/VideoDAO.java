package com.myplaylist.dao;

import com.myplaylist.db.Database;
import com.myplaylist.model.Video;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO {

    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();
        String sql = "SELECT * FROM videos";

        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("creator"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("genre"),
                        rs.getDouble("duration"),
                        rs.getString("thumbnail_path") // <--- Ambil Path
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    public boolean addVideo(Video video) {
        // Query UPDATE: Tambah kolom thumbnail_path
        String sql = "INSERT INTO videos(title, creator, category, year, genre, duration, thumbnail_path) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getTitle());
            pstmt.setString(2, video.getCreator());
            pstmt.setString(3, video.getCategory());
            pstmt.setInt(4, video.getYear());
            pstmt.setString(5, video.getGenre());
            pstmt.setDouble(6, video.getDuration());
            pstmt.setString(7, video.getThumbnailPath()); // <--- Simpan Path
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVideo(Video video) {
        // Query UPDATE: Tambah kolom thumbnail_path
        String sql = "UPDATE videos SET title = ?, creator = ?, category = ?, year = ?, genre = ?, duration = ?, thumbnail_path = ? WHERE id = ?";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getTitle());
            pstmt.setString(2, video.getCreator());
            pstmt.setString(3, video.getCategory());
            pstmt.setInt(4, video.getYear());
            pstmt.setString(5, video.getGenre());
            pstmt.setDouble(6, video.getDuration());
            pstmt.setString(7, video.getThumbnailPath()); // <--- Update Path
            pstmt.setInt(8, video.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVideo(int id) {
        String sql = "DELETE FROM videos WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
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

            try (Connection conn = Database.getInstance().getConnection();

                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    return new Video(

                            rs.getInt("id"),

                            rs.getString("title"),

                            rs.getString("creator"),

                            rs.getString("category"),

                            rs.getInt("year"),

                            rs.getString("genre"),

                            rs.getDouble("duration"),

                            rs.getString("thumbnail_path") // <--- Ambil Path

                    );

                }

            } catch (SQLException e) {

                e.printStackTrace();

            }

            return null;

        }

    

        public List<Video> searchVideos(String query) {

            List<Video> videos = new ArrayList<>();

            String sql = "SELECT * FROM videos WHERE LOWER(title) LIKE ? OR LOWER(creator) LIKE ?";

            try (Connection conn = Database.getInstance().getConnection();

                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String searchQuery = "%" + query.toLowerCase() + "%";

                pstmt.setString(1, searchQuery);

                pstmt.setString(2, searchQuery);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {

                    videos.add(new Video(

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

            return videos;

        }

    }

    