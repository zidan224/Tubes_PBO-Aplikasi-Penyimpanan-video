package com.example;

import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.Video;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class VideoFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField creatorField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField durationField;
    @FXML
    private Label thumbnailPathLabel;
    @FXML
    private Label messageLabel;

    private AppFacade appFacade;
    private File selectedThumbnailFile; // To hold the chosen file before saving
    private Video videoBeingEdited; // New field for edit mode

    public void setAppFacade(AppFacade appFacade) {
        this.appFacade = appFacade;
    }

    // New method for edit mode
    public void initData(Video videoToEdit, AppFacade appFacade) {
        this.appFacade = appFacade;
        this.videoBeingEdited = videoToEdit;

        if (videoToEdit != null) {
            // Populate fields for editing
            titleField.setText(videoToEdit.getTitle());
            creatorField.setText(videoToEdit.getCreator());
            categoryField.setText(videoToEdit.getCategory());
            yearField.setText(String.valueOf(videoToEdit.getYear()));
            genreField.setText(videoToEdit.getGenre());
            durationField.setText(String.valueOf(videoToEdit.getDuration()));
            thumbnailPathLabel.setText(videoToEdit.getThumbnailPath() != null ? new File(videoToEdit.getThumbnailPath()).getName() : "No file chosen");

            // Change stage title - REMOVED: caller (SecondaryController) will set stage title
            // Stage stage = (Stage) titleField.getScene().getWindow();
            // stage.setTitle("Edit Video: " + videoToEdit.getTitle());
        } else {
            // Ensure title is "Add New Video" for add mode - REMOVED: caller will set stage title
            // Stage stage = (Stage) titleField.getScene().getWindow();
            // stage.setTitle("Add New Video");
        }
    }


    @FXML
    void handleChooseThumbnail(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Thumbnail");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        selectedThumbnailFile = fileChooser.showOpenDialog(new Stage());
        if (selectedThumbnailFile != null) {
            thumbnailPathLabel.setText(selectedThumbnailFile.getName());
        } else {
            thumbnailPathLabel.setText(videoBeingEdited != null && videoBeingEdited.getThumbnailPath() != null ? new File(videoBeingEdited.getThumbnailPath()).getName() : "No file chosen");
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String title = titleField.getText();
        String creator = creatorField.getText();
        String category = categoryField.getText();
        String genre = genreField.getText();
        String yearStr = yearField.getText();
        String durationStr = durationField.getText();
        String thumbnailRelativePath = null;

        if (title.isEmpty() || creator.isEmpty() || category.isEmpty() || yearStr.isEmpty() || genre.isEmpty() || durationStr.isEmpty()) {
            messageLabel.setText("Semua kolom harus diisi.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            double duration = Double.parseDouble(durationStr);

            // Handle thumbnail file
            if (selectedThumbnailFile != null) {
                // Copy new thumbnail to project's images directory
                File projectRoot = new File(".").getAbsoluteFile().getParentFile();
                File imagesDir = new File(projectRoot, "images");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs(); // Create if not exists
                }

                String fileName = UUID.randomUUID().toString() + "_" + selectedThumbnailFile.getName();
                Path destinationPath = Paths.get(imagesDir.getAbsolutePath(), fileName);
                Files.copy(selectedThumbnailFile.toPath(), destinationPath);
                thumbnailRelativePath = "images" + File.separator + fileName; // Store relative path
            } else if (videoBeingEdited != null && videoBeingEdited.getThumbnailPath() != null) {
                // No new thumbnail selected, reuse existing one in edit mode
                thumbnailRelativePath = videoBeingEdited.getThumbnailPath();
            } else {
                messageLabel.setText("Thumbnail harus dipilih.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }


            boolean success;
            if (videoBeingEdited == null) { // Add mode
                Video newVideo = new Video(
                        0, // ID will be generated by DB
                        title,
                        creator,
                        category,
                        year,
                        genre,
                        duration,
                        thumbnailRelativePath
                );
                success = appFacade.addVideo(newVideo);
                if (success) {
                    messageLabel.setText("Video berhasil ditambahkan!");
                } else {
                    messageLabel.setText("Gagal menambahkan video. Silakan coba lagi.");
                }
            } else { // Edit mode
                Video updatedVideo = new Video(
                        videoBeingEdited.getId(), // Keep original ID
                        title,
                        creator,
                        category,
                        year,
                        genre,
                        duration,
                        thumbnailRelativePath
                );
                success = appFacade.updateVideo(updatedVideo);
                if (success) {
                    messageLabel.setText("Video berhasil diperbarui!");
                } else {
                    messageLabel.setText("Gagal memperbarui video. Silakan coba lagi.");
                }
            }
            
            if (success) {
                messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                // Clear fields only if adding or if confirmed closed
                if (videoBeingEdited == null) {
                    titleField.clear();
                    creatorField.clear();
                    categoryField.clear();
                    yearField.clear();
                    genreField.clear();
                    durationField.clear();
                    thumbnailPathLabel.setText("No file chosen");
                    selectedThumbnailFile = null;
                }
                ((Stage) messageLabel.getScene().getWindow()).close();
            } else {
                messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Tahun dan Durasi harus berupa angka.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        } catch (IOException e) {
            messageLabel.setText("Gagal menyimpan thumbnail: " + e.getMessage());
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}
