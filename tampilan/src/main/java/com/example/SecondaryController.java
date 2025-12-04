package com.example;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.Video;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecondaryController {

    @FXML
    private TextField searchField;
    @FXML
    private TilePane videoGrid;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button editButton;
    @FXML
    private Button myWatchlistButton;

    private AppFacade appFacade;
    private Video selectedVideo;
    private VBox selectedVideoCard;
    private boolean isShowingWatchlist = false; // New field

    private final String NORMAL_STYLE = "-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;";
    private final String SELECTED_STYLE = "-fx-border-color: #007bff; -fx-border-width: 3; -fx-background-color: #E0F2F7; -fx-background-radius: 5;";

    public void setAppFacade(AppFacade appFacade) {
        this.appFacade = appFacade;
        loadAllVideos(); 
        setupUserControls();
    }

    private void setupUserControls() {
        boolean isAdmin = "admin".equals(appFacade.getCurrentUser().getRole());
        addButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        addButton.setManaged(isAdmin);
        deleteButton.setManaged(isAdmin);
        editButton.setManaged(isAdmin);

        searchButton.setOnAction(event -> handleSearch());
        searchField.setOnAction(event -> handleSearch());
        myWatchlistButton.setOnAction(event -> handleMyWatchlist());
    }

    private void loadAllVideos() {
        if (appFacade == null) {
            System.out.println("AppFacade is not initialized.");
            return;
        }
        isShowingWatchlist = false; // Set flag
        displayVideos(appFacade.getAllVideos());
    }

    private void displayVideos(List<Video> videos) {
        videoGrid.getChildren().clear();
        selectedVideo = null;
        if (selectedVideoCard != null) {
            selectedVideoCard.setStyle(NORMAL_STYLE);
            selectedVideoCard = null;
        }
        
        for (Video video : videos) {
            try {
                VBox card = createVideoCard(video);
                videoGrid.getChildren().add(card);
            } catch (FileNotFoundException e) {
                System.err.println("Could not load thumbnail for video: " + video.getTitle() + " at path: " + video.getThumbnailPath());
                videoGrid.getChildren().add(createPlaceholderCard(video));
            }
        }
    }

    private VBox createVideoCard(Video video) throws FileNotFoundException {
        VBox card = new VBox(5);
        card.setPadding(new Insets(5));
        card.setStyle(NORMAL_STYLE);
        card.setPrefWidth(180);
        card.setUserData(video);
        
        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addToWatchlistMenuItem = new MenuItem("Add to Watchlist");
        addToWatchlistMenuItem.setOnAction(event -> handleAddVideoToWatchlist(video));
        contextMenu.getItems().add(addToWatchlistMenuItem);

        if (isShowingWatchlist) { // Conditionally add Remove from Watchlist
            MenuItem removeFromWatchlistMenuItem = new MenuItem("Remove from Watchlist");
            removeFromWatchlistMenuItem.setOnAction(event -> handleRemoveVideoFromWatchlist(video));
            contextMenu.getItems().add(removeFromWatchlistMenuItem);
        }

        card.setOnContextMenuRequested(event -> contextMenu.show(card, event.getScreenX(), event.getScreenY()));

        card.setOnMouseClicked(event -> handleCardClick(video, card, event));

        // Thumbnail
        String relativePath = video.getThumbnailPath();
        File projectRoot = new File(".").getAbsoluteFile().getParentFile();
        File imageFile = new File(projectRoot, relativePath);
        
        System.out.println("Attempting to load image from: " + imageFile.getAbsolutePath());

        Image thumbnail = new Image(imageFile.toURI().toString());
        if (thumbnail.isError()) {
             throw new FileNotFoundException("Image failed to load from URI: " + imageFile.toURI().toString());
        }

        ImageView thumbnailView = new ImageView(thumbnail);
        thumbnailView.setFitWidth(170);
        thumbnailView.setFitHeight(100);
        thumbnailView.setPreserveRatio(true);

        // Title
        Label titleLabel = new Label(video.getTitle());
        titleLabel.setFont(new Font("System Bold", 14));
        titleLabel.setWrapText(true);

        // Creator
        Label creatorLabel = new Label(video.getCreator());
        creatorLabel.setFont(new Font("System", 12));
        creatorLabel.setStyle("-fx-text-fill: #606060;");

        // Duration and Year
        Label durationYearLabel = new Label(String.format("Duration: %.1f min | Year: %d", video.getDuration(), video.getYear()));
        durationYearLabel.setFont(new Font("System", 11));
        durationYearLabel.setStyle("-fx-text-fill: #909090;");
        
        card.getChildren().addAll(thumbnailView, titleLabel, creatorLabel, durationYearLabel);
        return card;
    }

    private void handleCardClick(Video video, VBox card, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            openVideoPlayer(video);
        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            if (selectedVideoCard != null && selectedVideoCard != card) {
                selectedVideoCard.setStyle(NORMAL_STYLE);
            }

            if (card == selectedVideoCard) {
                card.setStyle(NORMAL_STYLE);
                selectedVideo = null;
                selectedVideoCard = null;
            } else {
                card.setStyle(SELECTED_STYLE);
                selectedVideo = video;
                selectedVideoCard = card;
            }
        }
    }


    private void openVideoPlayer(Video video) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Player.fxml"));
            Parent root = loader.load();

            PlayerController playerController = loader.getController();
            Scene currentScene = videoGrid.getScene();

            List<Video> currentPlaylist = new ArrayList<>();
            int currentVideoIndex = -1;
            int i = 0;
            for (javafx.scene.Node node : videoGrid.getChildren()) {
                if (node instanceof VBox) {
                    Video v = (Video) node.getUserData();
                    if (v != null) {
                        currentPlaylist.add(v);
                        if (v.getId() == video.getId()) {
                            currentVideoIndex = i;
                        }
                        i++;
                    }
                }
            }
            
            playerController.initData(video, currentPlaylist, currentVideoIndex, appFacade, currentScene);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(video.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VBox createPlaceholderCard(Video video) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(5));
        card.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefSize(180, 150);
        
        card.setUserData(video);
        card.setOnMouseClicked(event -> handleCardClick(video, card, event));

        Label titleLabel = new Label(video.getTitle());
        titleLabel.setWrapText(true);
        Label errorLabel = new Label("Image not found");
        card.getChildren().addAll(titleLabel, errorLabel);
        return card;
    }

    @FXML
    void handleAddVideo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VideoForm.fxml"));
            Parent root = loader.load();
            VideoFormController controller = loader.getController();
            controller.setAppFacade(appFacade);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Video");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadAllVideos(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleEditVideo(ActionEvent event) {
        if (selectedVideo == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Video Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a video to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VideoForm.fxml"));
            Parent root = loader.load();
            VideoFormController controller = loader.getController();
            controller.initData(selectedVideo, appFacade);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Video: " + selectedVideo.getTitle());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadAllVideos(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteVideo(ActionEvent event) {
        if (selectedVideo == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Video Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a video to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Video: " + selectedVideo.getTitle() + "?");
        confirmAlert.setContentText("Are you sure you want to delete this video?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = appFacade.deleteVideo(selectedVideo.getId());
            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deletion Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Video '" + selectedVideo.getTitle() + "' deleted successfully.");
                successAlert.showAndWait();
                loadAllVideos(); // Refresh dashboard
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Deletion Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to delete video '" + selectedVideo.getTitle() + "'.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        appFacade.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MyPlaylist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            displayVideos(new ArrayList<>()); // Strict search: empty query means empty results
        } else {
            displayVideos(appFacade.searchVideos(query));
        }
    }

    @FXML
    void handleMyWatchlist() { // New method
        if (appFacade == null || appFacade.getCurrentUser() == null) {
            System.err.println("User not logged in or AppFacade not initialized.");
            return;
        }
        isShowingWatchlist = true; // Set flag
        displayVideos(appFacade.getMyWatchlist());
    }
    
    // New method for adding video to watchlist
    private void handleAddVideoToWatchlist(Video video) {
        if (appFacade == null || appFacade.getCurrentUser() == null) {
            showAlert(Alert.AlertType.WARNING, "Not Logged In", "Please log in to add videos to your watchlist.");
            return;
        }
        String result = appFacade.addToWatchlist(video.getId());
        showAlert(Alert.AlertType.INFORMATION, "Add to Watchlist", result);
    }
    
    // Helper method to show alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // New method for removing video from watchlist
    private void handleRemoveVideoFromWatchlist(Video video) {
        if (appFacade == null || appFacade.getCurrentUser() == null) {
            showAlert(Alert.AlertType.WARNING, "Not Logged In", "Please log in to manage your watchlist.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Remove from Watchlist");
        confirmAlert.setHeaderText("Remove video '" + video.getTitle() + "' from your watchlist?");
        confirmAlert.setContentText("Are you sure you want to remove this video?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = appFacade.removeFromWatchlist(video.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Remove from Watchlist", "Video '" + video.getTitle() + "' removed successfully.");
                displayVideos(appFacade.getMyWatchlist()); // Refresh watchlist view
            } else {
                showAlert(Alert.AlertType.ERROR, "Remove from Watchlist Failed", "Failed to remove video '" + video.getTitle() + "'.");
            }
        }
    }
}