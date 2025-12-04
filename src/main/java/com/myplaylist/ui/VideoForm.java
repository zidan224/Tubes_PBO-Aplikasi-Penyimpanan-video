package com.myplaylist.ui;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.Video;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class VideoForm extends JDialog {
    // Form Inputs
    private JTextField titleField = new JTextField(20);
    private JTextField creatorField = new JTextField(20);
    private JTextField categoryField = new JTextField(20);
    private JTextField yearField = new JTextField(20);
    private JTextField genreField = new JTextField(20);
    private JTextField durationField = new JTextField(20);
    
    // Image Upload Components
    private JLabel imagePreviewLabel = new JLabel("No Image", SwingConstants.CENTER);
    private JButton uploadButton = new JButton("Upload Thumbnail");
    private String selectedImagePath = null; // Menyimpan path gambar

    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    private transient AppFacade appFacade;
    private transient Video video;

    public VideoForm(Frame parent, AppFacade appFacade, Video video) {
        super(parent, "Video Details", true);
        this.appFacade = appFacade;
        this.video = video;

        setLayout(new BorderLayout(10, 10));

        // Panel Kiri (Input Text)
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(new JLabel("Title:")); inputPanel.add(titleField);
        inputPanel.add(new JLabel("Creator:")); inputPanel.add(creatorField);
        inputPanel.add(new JLabel("Category:")); inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Year:")); inputPanel.add(yearField);
        inputPanel.add(new JLabel("Genre:")); inputPanel.add(genreField);
        inputPanel.add(new JLabel("Duration (mins):")); inputPanel.add(durationField);
        inputPanel.add(new JLabel("Thumbnail:")); inputPanel.add(uploadButton);

        // Panel Kanan (Preview Gambar)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        imagePreviewLabel.setPreferredSize(new Dimension(180, 120));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);
        
        // Panel Bawah (Tombol)
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveButton);
        btnPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.EAST);
        add(btnPanel, BorderLayout.SOUTH);

        // Isi data jika mode Edit
        if (video != null) {
            titleField.setText(video.getTitle());
            creatorField.setText(video.getCreator());
            categoryField.setText(video.getCategory());
            yearField.setText(String.valueOf(video.getYear()));
            genreField.setText(video.getGenre());
            durationField.setText(String.valueOf(video.getDuration()));
            
            // Load gambar jika ada
            if (video.getThumbnailPath() != null) {
                selectedImagePath = video.getThumbnailPath();
                displayImage(selectedImagePath);
            }
        }

        // Event Listeners
        uploadButton.addActionListener(e -> chooseImage());
        saveButton.addActionListener(e -> saveVideo());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Buat folder 'images' jika belum ada
                File destFolder = new File("images");
                if (!destFolder.exists()) destFolder.mkdir();
                
                // Copy file ke folder project dengan nama unik
                String newFileName = System.currentTimeMillis() + "_" + file.getName();
                File destFile = new File(destFolder, newFileName);
                
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                selectedImagePath = destFile.getPath(); // Simpan path relatif
                displayImage(selectedImagePath);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal upload gambar: " + ex.getMessage());
            }
        }
    }

    private void displayImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(img));
            imagePreviewLabel.setText("");
        } catch (Exception e) {
            imagePreviewLabel.setText("Image Error");
        }
    }

    private void saveVideo() {
        try {
            String title = titleField.getText();
            String creator = creatorField.getText();
            String category = categoryField.getText();
            int year = Integer.parseInt(yearField.getText());
            String genre = genreField.getText();
            double duration = Double.parseDouble(durationField.getText());

            if (video == null) {
                // ADD VIDEO BARU (ID = 0, + Path Gambar)
                Video newVideo = new Video(0, title, creator, category, year, genre, duration, selectedImagePath);
                if (appFacade.addVideo(newVideo)) {
                    JOptionPane.showMessageDialog(this, "Video added successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add video.");
                }
            } else {
                // UPDATE VIDEO
                video.setTitle(title);
                video.setCreator(creator);
                video.setCategory(category);
                video.setYear(year);
                video.setGenre(genre);
                video.setDuration(duration);
                video.setThumbnailPath(selectedImagePath); // Update Path Gambar
                
                if (appFacade.updateVideo(video)) {
                    JOptionPane.showMessageDialog(this, "Video updated successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update video.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year and Duration must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}