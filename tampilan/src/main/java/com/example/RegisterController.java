package com.example;

import com.myplaylist.facade.AppFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private AppFacade appFacade;

    public RegisterController() {
        this.appFacade = new AppFacade();
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password cannot be empty.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        boolean success = appFacade.register(username, password);

        if (success) {
            messageLabel.setText("Registration successful! You can now login.");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } else {
            messageLabel.setText("Registration failed. Username might already exist.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    void handleBackToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("MyPlaylist");
    }
}
