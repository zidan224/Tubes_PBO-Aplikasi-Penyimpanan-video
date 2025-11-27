package com.myplaylist.ui;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.User;
import com.myplaylist.model.Video;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlaylistGUI extends JFrame {
    private AppFacade appFacade;
    private JTable videoTable;
    private DefaultTableModel tableModel;
    private User loggedInUser;
    private JFrame loginFrame;
    
    public PlaylistGUI(User user, JFrame loginFrame) { 
        this.loggedInUser = user;
        this.loginFrame = loginFrame;
        this.appFacade = new AppFacade(); 
        this.appFacade.setCurrentUser(user);
        setTitle("My Playlist App - Welcome " + user.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 50, 50));
        
        JLabel titleLabel = new JLabel("Library Video (" + user.getRole() + ")");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);

        if ("admin".equalsIgnoreCase(user.getRole())) {
            add(createAdminDashboard(), BorderLayout.CENTER);
        } else {
            add(createUserDashboard(), BorderLayout.CENTER);
        }
    }

    // --- DASHBOARD ADMIN ---
    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Creator", "Category", "Year", "Genre", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0);
        videoTable = new JTable(tableModel);
        loadVideoData();

        JScrollPane scrollPane = new JScrollPane(videoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Video");
        JButton btnEdit = new JButton("Edit Video");
        JButton btnDelete = new JButton("Delete Video");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> showVideoForm(null));
        
        btnEdit.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Video video = appFacade.getVideoById(id);
                showVideoForm(video);
            } else {
                JOptionPane.showMessageDialog(this, "Select a video to edit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (appFacade.deleteVideo(id)) {
                        loadVideoData();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a video to delete.");
            }
        });

        btnRefresh.addActionListener(e -> loadVideoData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- DASHBOARD USER ---
    private JPanel createUserDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Creator", "Category", "Genre", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        videoTable = new JTable(tableModel);
        loadVideoData();

        JScrollPane scrollPane = new JScrollPane(videoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        
        JButton btnAddToWatchlist = new JButton("➕ Add to My Watchlist");
        JButton btnOpenWatchlist = new JButton("📂 Open My Watchlist");
        JButton btnRefresh = new JButton("🔄 Refresh Library");
        JButton btnPlay = new JButton("▶ Play Selected");
        btnPlay.setBackground(new Color(50, 200, 50));
        btnPlay.setForeground(Color.WHITE);

        btnAddToWatchlist.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                int videoId = (int) tableModel.getValueAt(selectedRow, 0);
                String result = appFacade.addToWatchlist(videoId);
                JOptionPane.showMessageDialog(this, result);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih video dulu untuk ditambahkan.");
            }
        });

        btnOpenWatchlist.addActionListener(e -> showWatchlistDialog());

        // UPDATE DISINI: Buka VideoPlayer Baru
        btnPlay.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                List<Video> allVideos = appFacade.getAllVideos(); 
                new VideoPlayer(this, allVideos, selectedRow).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih video yang ingin diputar.");
            }
        });

        btnRefresh.addActionListener(e -> loadVideoData());

        buttonPanel.add(btnPlay);
        buttonPanel.add(btnAddToWatchlist);
        buttonPanel.add(btnOpenWatchlist);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- WATCHLIST DIALOG ---
    private void showWatchlistDialog() {
        JDialog dialog = new JDialog(this, "My Personal Watchlist", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        String[] columns = {"ID", "Title", "Creator", "Genre"};
        DefaultTableModel watchModel = new DefaultTableModel(columns, 0);
        JTable watchTable = new JTable(watchModel);
        
        List<Video> myVideos = appFacade.getMyWatchlist();
        if (myVideos != null) {
            for (Video v : myVideos) {
                watchModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getCreator(), v.getGenre()});
            }
        }

        JScrollPane scroll = new JScrollPane(watchTable);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnRemove = new JButton("Remove from Watchlist");
        JButton btnPlayWatchlist = new JButton("▶ Play Watchlist");

        btnRemove.addActionListener(e -> {
            int row = watchTable.getSelectedRow();
            if (row != -1) {
                int vidId = (int) watchModel.getValueAt(row, 0);
                if (appFacade.removeFromWatchlist(vidId)) {
                    watchModel.removeRow(row);
                    JOptionPane.showMessageDialog(dialog, "Video dihapus dari watchlist.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih video yang mau dihapus.");
            }
        });

        // UPDATE DISINI: Play seluruh Watchlist
        btnPlayWatchlist.addActionListener(e -> {
            List<Video> watchlist = appFacade.getMyWatchlist();
            if (watchlist != null && !watchlist.isEmpty()) {
                new VideoPlayer((Frame)SwingUtilities.getWindowAncestor(dialog), watchlist, 0).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(dialog, "Watchlist kosong.");
            }
        });

        btnPanel.add(btnPlayWatchlist);
        btnPanel.add(btnRemove);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadVideoData() {
        tableModel.setRowCount(0);
        List<Video> videos = appFacade.getAllVideos();
        for (Video v : videos) {
            if ("admin".equals(loggedInUser.getRole())) {
                tableModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getCreator(), v.getCategory(), v.getYear(), v.getGenre(), v.getDuration()});
            } else {
                tableModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getCreator(), v.getCategory(), v.getGenre(), v.getDuration()});
            }
        }
    }

    private void showVideoForm(Video video) {
        VideoForm form = new VideoForm(this, appFacade, video);
        form.setVisible(true);
        loadVideoData();
    }

    private void logout() {
        this.appFacade.logout();
        this.dispose();
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        } else {
             new LoginGUI().setVisible(true);
        }
    }
}