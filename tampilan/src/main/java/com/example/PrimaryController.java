package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.myplaylist.facade.AppFacade;
import com.myplaylist.model.User;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class PrimaryController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AppFacade appFacade;

    public PrimaryController() {
        this.appFacade = new AppFacade();
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        boolean loginSuccess = appFacade.login(username, password);

        if (loginSuccess) {
            // Login successful
            errorLabel.setText(""); // Clear error message

            // Load the secondary view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();

            // Get the controller of the secondary view
            SecondaryController secondaryController = loader.getController();

            // Pass the AppFacade instance to the next controller
            secondaryController.setAppFacade(this.appFacade);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MyPlaylist Dashboard");
            stage.show();

        } else {
            // Login failed
            errorLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Register New Account");
    }
}
