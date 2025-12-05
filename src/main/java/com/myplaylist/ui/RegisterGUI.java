package com.myplaylist.ui;

import com.myplaylist.dao.UserDAO;
import com.myplaylist.model.User;

import javax.swing.*;
import java.awt.*;


public class RegisterGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton loginButton;
    private static final String ERROR_TITLE = "Error";  

    public RegisterGUI() {
        setTitle("Register");
        setSize(400, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        registerButton = new JButton("Register");
        loginButton = new JButton("Back to Login");

        panel.add(registerButton);
        panel.add(loginButton);

        add(panel);

        registerButton.addActionListener(e -> handleRegister());
        loginButton.addActionListener(e -> openLogin());
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User newUser = new User(username, password, "user"); // Always register as a "user"

        if (userDAO.addUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Welcome.");
            dispose(); // Close the register window
            new PlaylistGUI(newUser, new LoginGUI()).setVisible(true); // Open the main app window
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username might already exist.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLogin() {
        dispose();
        new LoginGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterGUI().setVisible(true));
    }
}
