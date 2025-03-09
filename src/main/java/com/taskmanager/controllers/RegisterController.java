package com.taskmanager.controllers;

import com.taskmanager.dao.UserDAO;
import com.taskmanager.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        // Clear any previous error messages
        errorLabel.setVisible(false);

        // Get and trim all input values
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate all inputs
        if (!validateInputs(username, email, password, confirmPassword)) {
            return;
        }

        try {
            // Create new user
            User newUser = new User(username, password, email);
            userDAO.createUser(newUser);

            // Show success message
            showSuccess("Compte créé avec succès!");

            // Redirect to login page after short delay
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                    loadLoginView();
                } catch (Exception e) {
                    showError("Erreur lors de la redirection: " + e.getMessage());
                }
            });

        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint")) {
                showError("Ce nom d'utilisateur ou email est déjà utilisé");
            } else {
                showError("Erreur lors de la création du compte: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Tous les champs sont obligatoires");
            return false;
        }

        // Validate username length
        if (username.length() < 3 || username.length() > 50) {
            showError("Le nom d'utilisateur doit contenir entre 3 et 50 caractères");
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Format d'email invalide");
            return false;
        }

        // Validate password strength
        if (password.length() < 8) {
            showError("Le mot de passe doit contenir au moins 8 caractères");
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return false;
        }

        return true;
    }

    @FXML
    private void handleBackToLogin() {
        try {
            loadLoginView();
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page de connexion: " + e.getMessage());
        }
    }

    private void loadLoginView() throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/com/taskmanager/login.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(loginView));
        stage.setTitle("Task Manager - Login");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: green;");
    }
}