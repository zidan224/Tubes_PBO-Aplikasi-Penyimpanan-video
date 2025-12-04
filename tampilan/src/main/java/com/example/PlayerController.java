package com.example;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.Video;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class PlayerController {

    @FXML
    private MediaView mediaView;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Slider progressBar;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label totalTimeLabel;

    private Scene previousScene;
    private AppFacade appFacade;
    private MediaPlayer mediaPlayer;
    private List<Video> playlist; // New field
    private int currentVideoIndex; // New field

    public void initData(Video videoToPlay, List<Video> playlist, int currentVideoIndex, AppFacade appFacade, Scene previousScene) {
        this.appFacade = appFacade;
        this.previousScene = previousScene;
        this.playlist = playlist;
        this.currentVideoIndex = currentVideoIndex;

        // Ensure currentVideoIndex is valid
        if (this.playlist == null || this.playlist.isEmpty() || currentVideoIndex < 0 || currentVideoIndex >= this.playlist.size()) {
            System.err.println("Invalid playlist or video index provided.");
            // Optionally, show an alert or return to dashboard
            handleBack(null); // Go back if no video to play
            return;
        }

        playVideo(this.playlist.get(this.currentVideoIndex));
    }

    private void playVideo(Video video) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Release resources
        }

        String placeholderVideoPath = "videos/sample.mp4"; 
        File projectRoot = new File(".").getAbsoluteFile().getParentFile();
        File videoFile = new File(projectRoot, placeholderVideoPath);

        if (!videoFile.exists()) {
            System.err.println("Placeholder video not found at: " + videoFile.getAbsolutePath());
            System.err.println("Please ensure you have a 'videos' folder in your project root with a 'sample.mp4' file.");
            // Optionally, show an alert to the user
            return;
        }

        Media media = new Media(videoFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setAutoPlay(true);
        playPauseButton.setText("Pause");

        // Bind the MediaView size to the pane size for resizing
        mediaView.fitWidthProperty().bind(mainPane.widthProperty());
        mediaView.fitHeightProperty().bind(mainPane.heightProperty());

        // Update progress bar and time labels
        mediaPlayer.setOnReady(() -> {
            progressBar.setMin(0);
            progressBar.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            totalTimeLabel.setText(formatTime(mediaPlayer.getMedia().getDuration()));
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressBar.isValueChanging()) {
                progressBar.setValue(newTime.toSeconds());
            }
            currentTimeLabel.setText(formatTime(newTime));
        });

        progressBar.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (progressBar.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
        
        mediaPlayer.setOnEndOfMedia(() -> handleNext(null)); // Auto play next video
    }
    
    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }


    @FXML
    void handlePlayPause(ActionEvent event) {
        if (mediaPlayer == null) return;

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("Pause");
        }
    }

    @FXML
    void handlePrevious(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (currentVideoIndex > 0) {
            currentVideoIndex--;
            playVideo(playlist.get(currentVideoIndex));
            // Update stage title
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setTitle(playlist.get(currentVideoIndex).getTitle());
        } else {
            // Optionally loop or show message
            System.out.println("No previous video.");
            // Replay current video
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
        }
    }

    @FXML
    void handleNext(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (currentVideoIndex < playlist.size() - 1) {
            currentVideoIndex++;
            playVideo(playlist.get(currentVideoIndex));
            // Update stage title
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setTitle(playlist.get(currentVideoIndex).getTitle());
        } else {
            // Optionally loop or show message
            System.out.println("No next video. Looping to start.");
            currentVideoIndex = 0; // Loop back to the first video
            playVideo(playlist.get(currentVideoIndex));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setTitle(playlist.get(currentVideoIndex).getTitle());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Release resources
        }
        
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setScene(previousScene);
        stage.setTitle("MyPlaylist Dashboard"); // Reset title
    }
}
