package com.myplaylist.ui;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.User;
import com.myplaylist.model.Video;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlaylistGUI extends JFrame {
    // 1. Ganti DAO dengan Facade
    private AppFacade appFacade;
    private JTable videoTable;
    private DefaultTableModel tableModel;
    private User loggedInUser;
    private JFrame loginFrame; // Referensi ke login frame untuk logout
    
    public PlaylistGUI(User user, JFrame loginFrame) { // Tambah parameter loginFrame
        this.loggedInUser = user;
        this.loginFrame = loginFrame;
        this.appFacade = new AppFacade(); // Inisialisasi Facade
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

        // Main Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);

        // Cek Role untuk menentukan Dashboard
        if ("admin".equalsIgnoreCase(user.getRole())) {
            add(createAdminDashboard(), BorderLayout.CENTER);
        } else {
            add(createUserDashboard(), BorderLayout.CENTER);
        }
    }

    // --- DASHBOARD ADMIN (CRUD Tetap Ada) ---
    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel Video
        String[] columnNames = {"ID", "Title", "Creator", "Category", "Year", "Genre", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0);
        videoTable = new JTable(tableModel);
        loadVideoData(); // Load data pakai Facade

        JScrollPane scrollPane = new JScrollPane(videoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tombol CRUD Admin
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Video");
        JButton btnEdit = new JButton("Edit Video");
        JButton btnDelete = new JButton("Delete Video");
        JButton btnRefresh = new JButton("Refresh");

        // Action Listeners Admin
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

    // --- DASHBOARD USER (Fitur Watchlist & Play) ---
    private JPanel createUserDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabel Video (Semua Video)
        String[] columnNames = {"ID", "Title", "Creator", "Category", "Genre", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // User tidak bisa edit tabel langsung
            }
        };
        videoTable = new JTable(tableModel);
        loadVideoData();

        JScrollPane scrollPane = new JScrollPane(videoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Fitur User
        JPanel buttonPanel = new JPanel();
        
        JButton btnAddToWatchlist = new JButton("➕ Add to My Watchlist");
        JButton btnOpenWatchlist = new JButton("📂 Open My Watchlist");
        JButton btnRefresh = new JButton("🔄 Refresh Library");
        
        // Tombol PLAY (User ingin memutar video dari library utama)
        JButton btnPlay = new JButton("▶ Play Selected");
        btnPlay.setBackground(new Color(50, 200, 50));
        btnPlay.setForeground(Color.WHITE);

        // Logic Add to Watchlist
        btnAddToWatchlist.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                int videoId = (int) tableModel.getValueAt(selectedRow, 0);
                // Panggil Facade
                String result = appFacade.addToWatchlist(videoId);
                JOptionPane.showMessageDialog(this, result);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih video dulu untuk ditambahkan.");
            }
        });

        // Logic Open Watchlist (Membuka Jendela Baru)
        btnOpenWatchlist.addActionListener(e -> showWatchlistDialog());

        // Logic Play (Placeholder untuk Iterator Temanmu)
        btnPlay.addActionListener(e -> {
            int selectedRow = videoTable.getSelectedRow();
            if (selectedRow != -1) {
                String title = (String) tableModel.getValueAt(selectedRow, 1);
                JOptionPane.showMessageDialog(this, 
                    "▶ Memutar Video: " + title + "\n\n(Area ini siap diimplementasikan dengan Iterator Pattern)", 
                    "Video Player", JOptionPane.INFORMATION_MESSAGE);
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

    // --- FITUR TAMBAHAN: Jendela Watchlist ---
    private void showWatchlistDialog() {
        JDialog dialog = new JDialog(this, "My Personal Watchlist", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        // Tabel Watchlist
        String[] columns = {"ID", "Title", "Creator", "Genre"};
        DefaultTableModel watchModel = new DefaultTableModel(columns, 0);
        JTable watchTable = new JTable(watchModel);
        
        // Load data khusus milik user ini
        List<Video> myVideos = appFacade.getMyWatchlist();
        if (myVideos != null) {
            for (Video v : myVideos) {
                watchModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getCreator(), v.getGenre()});
            }
        }

        JScrollPane scroll = new JScrollPane(watchTable);
        dialog.add(scroll, BorderLayout.CENTER);

        // Tombol di Watchlist
        JPanel btnPanel = new JPanel();
        JButton btnRemove = new JButton("Remove from Watchlist");
        JButton btnPlayWatchlist = new JButton("▶ Play Watchlist");

        btnRemove.addActionListener(e -> {
            int row = watchTable.getSelectedRow();
            if (row != -1) {
                int vidId = (int) watchModel.getValueAt(row, 0);
                if (appFacade.removeFromWatchlist(vidId)) {
                    watchModel.removeRow(row); // Hapus dari tabel UI langsung
                    JOptionPane.showMessageDialog(dialog, "Video dihapus dari watchlist.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih video yang mau dihapus.");
            }
        });

        btnPlayWatchlist.addActionListener(e -> {
             JOptionPane.showMessageDialog(dialog, 
                    "▶ Memutar Semua Video di Watchlist\n\n(Iterator Pattern akan masuk di sini)", 
                    "Playlist Player", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(btnPlayWatchlist);
        btnPanel.add(btnRemove);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- Helper Methods ---
    private void loadVideoData() {
        tableModel.setRowCount(0);
        List<Video> videos = appFacade.getAllVideos();
        for (Video v : videos) {
            // Kolom menyesuaikan tampilan user/admin
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
        this.dispose(); // Tutup window playlist
        if (loginFrame != null) {
            loginFrame.setVisible(true); // Tampilkan lagi login screen
            // Kosongkan field login jika perlu (opsional)
        } else {
             new LoginGUI().setVisible(true);
        }
    }
}