package com.hms.controller;

import com.hms.dao.PatientDAO;
import com.hms.model.Patient;
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

public class PatientController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField contactField;
    @FXML private TextArea addressArea;
    @FXML private ComboBox<String> bloodGroupComboBox;
    @FXML private TextField emergencyContactField;
    @FXML private TextField searchField;
    
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Integer> idCol;
    @FXML private TableColumn<Patient, String> nameCol;
    @FXML private TableColumn<Patient, Integer> ageCol;
    @FXML private TableColumn<Patient, String> genderCol;
    @FXML private TableColumn<Patient, String> contactCol;
    
    private PatientDAO patientDAO;
    private ObservableList<Patient> patientList;
    private Patient selectedPatient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        patientDAO = new PatientDAO();
        patientList = FXCollections.observableArrayList();
        
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        bloodGroupComboBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        
        setupTable();
        loadPatients();
        
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPatient = newSelection;
                populateFields(selectedPatient);
            }
        });
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        patientTable.setItems(patientList);
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            patientList.setAll(patients);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load patients: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        Patient patient = Patient.builder()
                .name(nameField.getText())
                .age(Integer.parseInt(ageField.getText()))
                .gender(genderComboBox.getValue())
                .contact(contactField.getText())
                .address(addressArea.getText())
                .bloodGroup(bloodGroupComboBox.getValue())
                .emergencyContact(emergencyContactField.getText())
                .build();

        try {
            if (selectedPatient == null) {
                patientDAO.addPatient(patient);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient added successfully.");
            } else {
                patient.setId(selectedPatient.getId());
                patientDAO.updatePatient(patient);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient updated successfully.");
            }
            clearFields();
            loadPatients();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save patient: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this patient?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try {
                patientDAO.deletePatient(selectedPatient.getId());
                clearFields();
                loadPatients();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete patient: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadPatients();
            return;
        }
        try {
            List<Patient> patients = patientDAO.searchPatients(keyword);
            patientList.setAll(patients);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to search patients: " + e.getMessage());
        }
    }

    @FXML
    private void clearFields() {
        selectedPatient = null;
        nameField.clear();
        ageField.clear();
        genderComboBox.setValue(null);
        contactField.clear();
        addressArea.clear();
        bloodGroupComboBox.setValue(null);
        emergencyContactField.clear();
        patientTable.getSelectionModel().clearSelection();
    }

    private void populateFields(Patient patient) {
        nameField.setText(patient.getName());
        ageField.setText(String.valueOf(patient.getAge()));
        genderComboBox.setValue(patient.getGender());
        contactField.setText(patient.getContact());
        addressArea.setText(patient.getAddress());
        bloodGroupComboBox.setValue(patient.getBloodGroup());
        emergencyContactField.setText(patient.getEmergencyContact());
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || ageField.getText().isEmpty() || genderComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name, Age, and Gender are required.");
            return false;
        }
        try {
            Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Age must be a valid number.");
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
