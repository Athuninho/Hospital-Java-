package com.hms.controller;

import com.hms.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadView("dashboard");
    }

    @FXML
    private void showDashboard() {
        loadView("dashboard");
    }

    @FXML
    private void showPatients() {
        loadView("patient");
    }

    @FXML
    private void showDoctors() {
        loadView("doctor");
    }

    @FXML
    private void showAppointments() {
        loadView("appointment");
    }
    
    @FXML
    private void showBilling() {
        loadView("billing");
    }

    @FXML
    private void showMedicalHistory() {
        loadView("medical_history");
    }

    private void loadView(String fxml) {
        try {
            Parent view = App.loadFXML(fxml);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
