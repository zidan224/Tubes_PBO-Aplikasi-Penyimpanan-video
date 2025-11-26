package com.myplaylist.ui;

import com.myplaylist.dao.VideoDAO;
import com.myplaylist.model.User;
import com.myplaylist.model.Video;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PlaylistGUI extends JFrame {

    private User loggedInUser;
    private DefaultTableModel tableModel;
    private JTable videoTable;
    private VideoDAO videoDAO;

    public PlaylistGUI(User user) {
        this.loggedInUser = user;
        this.videoDAO = new VideoDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("My Playlist Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top panel for welcome message and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose(); // Close the current window
            new LoginGUI().setVisible(true); // Open the login window
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Role-specific dashboard
        if ("admin".equals(loggedInUser.getRole())) {
            mainPanel.add(createAdminDashboard(), BorderLayout.CENTER);
        } else {
            mainPanel.add(createUserDashboard(), BorderLayout.CENTER);
        }

        add(mainPanel);
    }

    private JPanel createAdminDashboard() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setBorder(BorderFactory.createTitledBorder("Admin Dashboard"));

        // Add video management panel
        adminPanel.add(createVideoPanel(), BorderLayout.CENTER);

        // Add admin-specific controls
        JPanel adminControls = new JPanel();
        adminControls.add(new JButton("Manage Users"));
        adminControls.add(new JButton("View System Logs"));
        adminPanel.add(adminControls, BorderLayout.SOUTH);

        return adminPanel;
    }

    private JPanel createUserDashboard() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Your Playlist"));
        userPanel.add(createVideoPanel(), BorderLayout.CENTER);
        return userPanel;
    }

    private JPanel createVideoPanel() {
        JPanel videoPanel = new JPanel(new BorderLayout());

        // Table for videos
        String[] columnNames = {"ID", "Title", "Creator", "Category", "Year", "Genre", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0);
        videoTable = new JTable(tableModel);

        refreshVideoTable();

        videoPanel.add(new JScrollPane(videoTable), BorderLayout.CENTER);

        // Buttons for video manipulation
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        addButton.addActionListener(e -> new VideoForm(this, videoDAO, null).setVisible(true));
        updateButton.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow >= 0) {
                int videoId = (int) tableModel.getValueAt(selectedRow, 0);
                Video videoToUpdate = videoDAO.getVideoById(videoId);
                new VideoForm(this, videoDAO, videoToUpdate).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a video to update.", "No Video Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow >= 0) {
                int videoId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this video?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    videoDAO.deleteVideo(videoId);
                    refreshVideoTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a video to delete.", "No Video Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        videoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return videoPanel;
    }

    public void refreshVideoTable() {
        // Clear the table
        tableModel.setRowCount(0);

        // Fetch and display videos
        for (Video video : videoDAO.getAllVideos()) {
            Object[] row = {
                    video.getId(),
                    video.getTitle(),
                    video.getCreator(),
                    video.getCategory(),
                    video.getYear(),
                    video.getGenre(),
                    video.getDuration()
            };
            tableModel.addRow(row);
        }
    }
}
