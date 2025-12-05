package com.myplaylist.facade;

import com.myplaylist.dao.UserDAO;
import com.myplaylist.dao.VideoDAO;
import com.myplaylist.dao.WatchlistDAO;
import com.myplaylist.model.User;
import com.myplaylist.model.Video;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level; // Import tambahan untuk Logger
import java.util.logging.Logger; // Import tambahan untuk Logger

public class AppFacade {
    
    private UserDAO userDAO;
    private VideoDAO videoDAO;
    private WatchlistDAO watchlistDAO;
    private User currentUser;
    private static final String ADMIN_TITLE = "admin"; 
    private static final Logger LOGGER = Logger.getLogger(AppFacade.class.getName());
    public AppFacade() {
        this.userDAO = new UserDAO();
        this.videoDAO = new VideoDAO();
        this.watchlistDAO = new WatchlistDAO();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // --- User Management ---
    public boolean login(String username, String password) {
        try {
            User user = userDAO.findUser(username, password);
            if (user != null) {
                this.currentUser = user;
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Login Error: {0}", e.getMessage());
        }
        return false;
    }

    public boolean register(String username, String password) {
        // Default role user
        User newUser = new User(username, password, "user");
        return userDAO.addUser(newUser);
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // --- Video Management (Admin CRUD & User View) ---
    public List<Video> getAllVideos() {
        return videoDAO.getAllVideos();
    }

    public List<Video> searchVideos(String query) {
        return videoDAO.searchVideos(query);
    }

    // Khusus Admin
    public boolean addVideo(Video video) {
        if (currentUser != null && ADMIN_TITLE.equals(currentUser.getRole())) {
            return videoDAO.addVideo(video);
        }
        return false;
    }

    public boolean updateVideo(Video video) {
        if (currentUser != null && ADMIN_TITLE.equals(currentUser.getRole())) {
            return videoDAO.updateVideo(video);
        }
        return false;
    }

    public boolean deleteVideo(int videoId) {
        if (currentUser != null && ADMIN_TITLE.equals(currentUser.getRole())) {
            return videoDAO.deleteVideo(videoId);
        }
        return false;
    }

    // --- Watchlist Management (Fitur Utama User) ---

    // Mengambil watchlist user yang sedang login
    public List<Video> getMyWatchlist() {
        if (currentUser == null)
            return Collections.emptyList();
        return watchlistDAO.getWatchlistByUserId(currentUser.getId());
    }

    // Menambahkan ke watchlist dengan Error Handling (Try-Catch & Logic Check)
    public String addToWatchlist(int videoId) {
        if (currentUser == null)
            return "Please login first.";

        try {
            boolean success = watchlistDAO.addVideoToWatchlist(currentUser.getId(), videoId);
            if (success) {
                return "Success: Video added to watchlist.";
            } else {
                return "Error: Video is already in your watchlist."; // Pesan error duplicate
            }
        } catch (Exception e) {
            return "System Error: " + e.getMessage();
        }
    }

    public boolean removeFromWatchlist(int videoId) {
        if (currentUser == null)
            return false;
        return watchlistDAO.removeVideoFromWatchlist(currentUser.getId(), videoId);
    }

    public Video getVideoById(int id) {
        return videoDAO.getVideoById(id);
    }
}