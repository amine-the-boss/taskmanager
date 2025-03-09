package com.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/taskmanager/login.fxml"));


        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/taskmanager/styles/styles.css").toExternalForm());

        primaryStage.setTitle("Task Manager - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}