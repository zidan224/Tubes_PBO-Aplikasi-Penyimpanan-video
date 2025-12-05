package com.myplaylist.dao;

import com.myplaylist.db.Database;
import com.myplaylist.model.Video;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO {
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_CREATOR = "creator";
    private static final String COL_CATEGORY = "category";
    private static final String COL_YEAR = "year";
    private static final String COL_GENRE = "genre";
    private static final String COL_DURATION = "duration";
    private static final String COL_THUMBNAIL = "thumbnail_path";
    private static final String SELECT_ALL_COLUMNS = 
        "SELECT " + COL_ID + ", " + COL_TITLE + ", " + COL_CREATOR + ", " + 
        COL_CATEGORY + ", " + COL_YEAR + ", " + COL_GENRE + ", " + 
        COL_DURATION + ", " + COL_THUMBNAIL + " FROM videos";

    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();
    
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_COLUMNS)) {

            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),
                        rs.getString(COL_TITLE),
                        rs.getString(COL_CREATOR),
                        rs.getString(COL_CATEGORY),
                        rs.getInt("year"),
                        rs.getString(COL_GENRE),
                        rs.getDouble(COL_DURATION),
                        rs.getString(COL_THUMBNAIL) // <--- Ambil Path
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
            String sql = SELECT_ALL_COLUMNS + " WHERE " + COL_ID + " = ?";

            try (Connection conn = Database.getInstance().getConnection();

                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    return new Video(

                            rs.getInt("id"),

                            rs.getString(COL_TITLE),

                            rs.getString(COL_CREATOR),

                            rs.getString(COL_CATEGORY),

                            rs.getInt("year"),

                            rs.getString(COL_GENRE),

                            rs.getDouble(COL_DURATION),

                            rs.getString(COL_THUMBNAIL) // <--- Ambil Path

                    );

                }

            } catch (SQLException e) {

                e.printStackTrace();

            }

            return null;

        }

    

        public List<Video> searchVideos(String query) {

            List<Video> videos = new ArrayList<>();

            String sql = SELECT_ALL_COLUMNS + " WHERE LOWER(" + COL_TITLE + ") LIKE ? OR LOWER(" + COL_CREATOR + ") LIKE ?";

            try (Connection conn = Database.getInstance().getConnection();

                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String searchQuery = "%" + query.toLowerCase() + "%";

                pstmt.setString(1, searchQuery);

                pstmt.setString(2, searchQuery);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {

                    videos.add(new Video(

                            rs.getInt("id"),

                            rs.getString(COL_TITLE),

                            rs.getString(COL_CREATOR),

                            rs.getString(COL_CATEGORY),

                            rs.getInt("year"),

                            rs.getString(COL_GENRE),

                            rs.getDouble(COL_DURATION),

                            rs.getString(COL_THUMBNAIL)

                    ));

                }

            } catch (SQLException e) {

                e.printStackTrace();

            }

            return videos;

        }

    }

    