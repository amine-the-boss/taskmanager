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

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                loadMainView(user);
            } else {
                showError("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showError("Erreur de connexion: " + e.getMessage());
        }
    }

    private void loadMainView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taskmanager/main.fxml"));
        Parent mainView = loader.load();

        MainController mainController = loader.getController();
        mainController.initData(user);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(mainView));
        stage.setTitle("Task Manager - " + user.getUsername());
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }


    @FXML
    private void handleRegister() throws IOException {
        Parent registerView = FXMLLoader.load(getClass().getResource("/com/taskmanager/register.fxml"));
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(registerView));
        stage.setTitle("Task Manager - Register");
    }
}