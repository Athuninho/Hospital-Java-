package com.hms.controller;

import com.hms.dao.DoctorDAO;
import com.hms.model.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField specializationField;
    @FXML private TextField contactField;
    @FXML private TextField feeField;
    @FXML private TextField daysField;
    @FXML private TextField hoursField;
    
    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, Integer> idCol;
    @FXML private TableColumn<Doctor, String> nameCol;
    @FXML private TableColumn<Doctor, String> specializationCol;
    @FXML private TableColumn<Doctor, Double> feeCol;
    @FXML private TableColumn<Doctor, String> daysCol;
    @FXML private TableColumn<Doctor, String> hoursCol;
    
    private DoctorDAO doctorDAO;
    private ObservableList<Doctor> doctorList;
    private Doctor selectedDoctor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doctorDAO = new DoctorDAO();
        doctorList = FXCollections.observableArrayList();
        
        setupTable();
        loadDoctors();
        
        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDoctor = newSelection;
                populateFields(selectedDoctor);
            }
        });
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        specializationCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        feeCol.setCellValueFactory(new PropertyValueFactory<>("consultationFee"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("availableDays"));
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("availableHours"));
        doctorTable.setItems(doctorList);
    }

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            doctorList.setAll(doctors);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load doctors: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        Doctor doctor = Doctor.builder()
                .name(nameField.getText())
                .specialization(specializationField.getText())
                .contact(contactField.getText())
                .consultationFee(Double.parseDouble(feeField.getText()))
                .availableDays(daysField.getText())
                .availableHours(hoursField.getText())
                .build();

        try {
            if (selectedDoctor == null) {
                doctorDAO.addDoctor(doctor);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor added successfully.");
            } else {
                doctor.setId(selectedDoctor.getId());
                doctorDAO.updateDoctor(doctor);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor updated successfully.");
            }
            clearFields();
            loadDoctors();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save doctor: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedDoctor == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a doctor to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this doctor?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try {
                doctorDAO.deleteDoctor(selectedDoctor.getId());
                clearFields();
                loadDoctors();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete doctor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearFields() {
        selectedDoctor = null;
        nameField.clear();
        specializationField.clear();
        contactField.clear();
        feeField.clear();
        daysField.clear();
        hoursField.clear();
        doctorTable.getSelectionModel().clearSelection();
    }

    private void populateFields(Doctor doctor) {
        nameField.setText(doctor.getName());
        specializationField.setText(doctor.getSpecialization());
        contactField.setText(doctor.getContact());
        feeField.setText(String.valueOf(doctor.getConsultationFee()));
        daysField.setText(doctor.getAvailableDays());
        hoursField.setText(doctor.getAvailableHours());
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || specializationField.getText().isEmpty() || feeField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name, Specialization, and Fee are required.");
            return false;
        }
        try {
            Double.parseDouble(feeField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Fee must be a valid number.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
