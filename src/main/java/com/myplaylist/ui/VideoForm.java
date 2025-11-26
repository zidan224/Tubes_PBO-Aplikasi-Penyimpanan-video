package com.myplaylist.ui;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.Video;

import javax.swing.*;
import java.awt.*;

public class VideoForm extends JDialog {
    // Sesuaikan field dengan Tabel Database Baru (Creator & Category)
    private JTextField titleField = new JTextField(20);
    private JTextField creatorField = new JTextField(20); // Dulu Artist
    private JTextField categoryField = new JTextField(20); // Dulu Album
    private JTextField yearField = new JTextField(20);
    private JTextField genreField = new JTextField(20);
    private JTextField durationField = new JTextField(20);
    
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    private AppFacade appFacade; // Ganti VideoDAO jadi AppFacade
    private Video video;

    // Constructor diperbarui menerima AppFacade
    public VideoForm(Frame parent, AppFacade appFacade, Video video) {
        super(parent, "Video Details", true);
        this.appFacade = appFacade;
        this.video = video;

        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Title:"));
        add(titleField);
        
        add(new JLabel("Creator:")); // Label baru
        add(creatorField);
        
        add(new JLabel("Category:")); // Label baru
        add(categoryField);
        
        add(new JLabel("Year:"));
        add(yearField);
        
        add(new JLabel("Genre:"));
        add(genreField);
        
        add(new JLabel("Duration (mins):"));
        add(durationField);
        
        add(saveButton);
        add(cancelButton);

        // Jika mode Edit, isi field dengan data lama
        if (video != null) {
            titleField.setText(video.getTitle());
            creatorField.setText(video.getCreator());
            categoryField.setText(video.getCategory());
            yearField.setText(String.valueOf(video.getYear()));
            genreField.setText(video.getGenre());
            durationField.setText(String.valueOf(video.getDuration()));
        }

        saveButton.addActionListener(e -> saveVideo());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
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
                // Mode ADD: ID 0 karena auto-increment di DB
                Video newVideo = new Video(0, title, creator, category, year, genre, duration);
                if (appFacade.addVideo(newVideo)) {
                    JOptionPane.showMessageDialog(this, "Video added successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add video.");
                }
            } else {
                // Mode UPDATE: Update objek video yang ada
                video.setTitle(title);
                video.setCreator(creator);
                video.setCategory(category);
                video.setYear(year);
                video.setGenre(genre);
                video.setDuration(duration);
                
                if (appFacade.updateVideo(video)) {
                    JOptionPane.showMessageDialog(this, "Video updated successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update video.");
                }
            }
            // Tidak perlu panggil refreshVideoTable() karena PlaylistGUI akan refresh sendiri setelah jendela ini tutup.
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year and Duration must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}