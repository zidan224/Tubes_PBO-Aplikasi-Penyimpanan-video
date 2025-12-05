package com.myplaylist.ui;

import com.myplaylist.iterator.Iterator;
import com.myplaylist.iterator.VideoListContainer; 
import com.myplaylist.model.Video;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoPlayer extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(VideoPlayer.class.getName());

    private JLabel imageLabel;
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JButton btnPrev; 
    private JButton btnPlay;
    private JButton btnNext;
    private static final String FONT_NAME = "Arial";
    
    private transient Iterator<Video> iterator;
    private boolean isPlaying = false;

    public VideoPlayer(Frame owner, List<Video> playlist, int startIndex) {
        super(owner, "MyWatchlist Player", true);
        setSize(700, 550);
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);

        VideoListContainer container = new VideoListContainer(playlist);
        iterator = container.getIterator();
        
        try {
            var method = iterator.getClass().getMethod("setIndex", int.class);
            method.invoke(iterator, startIndex);
        } catch (Exception e) { 
            LOGGER.log(Level.WARNING, "Warning: Could not set start index. Starting from beginning.", e);
        }

        imageLabel = new JLabel("No Thumbnail Available", SwingConstants.CENTER);
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setOpaque(true);
        imageLabel.setForeground(Color.GRAY);
        add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(new Color(30, 30, 30));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        titleLabel = new JLabel("Title", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 22));
        
        infoLabel = new JLabel("Creator - Genre", SwingConstants.CENTER);
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        
        infoPanel.add(titleLabel);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.NORTH);

        JPanel controls = new JPanel();
        controls.setBackground(Color.BLACK);
        controls.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        btnPrev = new JButton("<< Back");
        btnPlay = new JButton("|> Play");
        btnNext = new JButton("Next >>");

        styleButton(btnPrev);
        styleButton(btnPlay);
        styleButton(btnNext);

        controls.add(btnPrev);
        controls.add(btnPlay);
        controls.add(btnNext);
        add(controls, BorderLayout.SOUTH);

        btnNext.addActionListener(e -> {
            if (iterator.hasNext()) {
                loadVideo(iterator.next());
            } else {
                JOptionPane.showMessageDialog(this, "End of Playlist");
            }
        });

        btnPrev.addActionListener(e -> {
            if (iterator.hasPrev()) {
                loadVideo(iterator.prev());
            } else {
                JOptionPane.showMessageDialog(this, "Start of Playlist");
            }
        });

        btnPlay.addActionListener(e -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                btnPlay.setText("|| Pause");
                btnPlay.setBackground(new Color(255, 100, 100));
            } else {
                btnPlay.setText("|> Play");
                btnPlay.setBackground(new Color(100, 255, 100));
            }
        });

        loadVideo(iterator.current());
    }

    private void loadVideo(Video v) {
        if (v == null) return;

        titleLabel.setText(v.getTitle());
        infoLabel.setText(v.getCreator() + " - " + v.getGenre() + " - " + v.getYear());
        
        isPlaying = true;
        btnPlay.setText("|| Pause");
        btnPlay.setBackground(new Color(255, 100, 100));

        if (v.getThumbnailPath() != null && !v.getThumbnailPath().isEmpty()) {
            File f = new File(v.getThumbnailPath());
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(v.getThumbnailPath());
                Image img = icon.getImage().getScaledInstance(600, 350, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText("");
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("Image File Not Found");
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("No Thumbnail");
        }
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 40));
    }
}