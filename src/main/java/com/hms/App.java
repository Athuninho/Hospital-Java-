package com.hms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.hms.service.EmailReminderService;

public class App extends Application {

    private static Scene scene;
    private EmailReminderService reminderService;

    @Override
    public void start(Stage stage) throws IOException {
        // Start Background Service
        reminderService = new EmailReminderService();
        reminderService.startService();
        
        scene = new Scene(loadFXML("main"), 1024, 768);
        scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Hospital Management System");
        
        // Stop service on exit
        stage.setOnCloseRequest(event -> reminderService.stopService());
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
