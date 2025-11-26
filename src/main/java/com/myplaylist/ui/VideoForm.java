package com.myplaylist.ui;

import com.myplaylist.dao.VideoDAO;
import com.myplaylist.model.Video;

import javax.swing.*;
import java.awt.*;

public class VideoForm extends JDialog {
    private JTextField titleField = new JTextField(20);
    private JTextField creatorField = new JTextField(20);
    private JTextField categoryField = new JTextField(20);
    private JTextField yearField = new JTextField(20);
    private JTextField genreField = new JTextField(20);
    private JTextField durationField = new JTextField(20);
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    private VideoDAO videoDAO;
    private Video video;
    private PlaylistGUI parent;

    public VideoForm(PlaylistGUI parent, VideoDAO videoDAO, Video video) {
        super(parent, "Video Details", true);
        this.parent = parent;
        this.videoDAO = videoDAO;
        this.video = video;

        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Creator:"));
        add(creatorField);
        add(new JLabel("Category:"));
        add(categoryField);
        add(new JLabel("Year:"));
        add(yearField);
        add(new JLabel("Genre:"));
        add(genreField);
        add(new JLabel("Duration:"));
        add(durationField);
        add(saveButton);
        add(cancelButton);

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
        // Validation can be added here
        String title = titleField.getText();
        String creator = creatorField.getText();
        String category = categoryField.getText();
        int year = Integer.parseInt(yearField.getText());
        String genre = genreField.getText();
        double duration = Double.parseDouble(durationField.getText());

        if (video == null) {
            // Add new video
            Video newVideo = new Video(0, title, creator, category, year, genre, duration);
            videoDAO.addVideo(newVideo);
        } else {
            // Update existing video
            video.setTitle(title);
            video.setCreator(creator);
            video.setCategory(category);
            video.setYear(year);
            video.setGenre(genre);
            video.setDuration(duration);
            videoDAO.updateVideo(video);
        }
        parent.refreshVideoTable();
        dispose();
    }
}
