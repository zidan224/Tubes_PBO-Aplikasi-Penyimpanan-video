package com.myplaylist.ui;

import com.myplaylist.auth.User;
import javax.swing.*;
import java.awt.*;

public class PlaylistGUI extends JFrame {

    private User loggedInUser;

    public PlaylistGUI(User user) {
        this.loggedInUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("My Playlist Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Role-specific dashboard
        if ("admin".equals(loggedInUser.getRole())) {
            mainPanel.add(createAdminDashboard(), BorderLayout.CENTER);
        } else {
            mainPanel.add(createUserDashboard(), BorderLayout.CENTER);
        }

        add(mainPanel);
    }

    private JPanel createAdminDashboard() {
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setBorder(BorderFactory.createTitledBorder("Admin Dashboard"));
        
        adminPanel.add(new JLabel("Admin-specific controls here."));
        adminPanel.add(new JButton("Manage Users"));
        adminPanel.add(new JButton("View System Logs"));
        
        return adminPanel;
    }

    private JPanel createUserDashboard() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createTitledBorder("Your Playlist"));

        userPanel.add(new JLabel("Your songs and playlists will be shown here."));
        userPanel.add(new JButton("Add New Song"));

        return userPanel;
    }
}
