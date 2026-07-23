package com.hms.controller;

import com.hms.dao.AppointmentDAO;
import com.hms.dao.MedicalHistoryDAO;
import com.hms.dao.PatientDAO;
import com.hms.model.Appointment;
import com.hms.model.MedicalHistory;
import com.hms.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalHistoryController implements Initializable {

    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Appointment> appointmentComboBox;
    @FXML private TextArea diagnosesArea;
    @FXML private TextArea prescriptionsArea;
    @FXML private TextArea allergiesArea;
    @FXML private TextArea testResultsArea;
    
    @FXML private TableView<MedicalHistory> historyTable;
    @FXML private TableColumn<MedicalHistory, LocalDateTime> dateCol;
    @FXML private TableColumn<MedicalHistory, String> diagnosesCol;
    @FXML private TableColumn<MedicalHistory, String> prescriptionsCol;
    
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private MedicalHistoryDAO historyDAO;
    private ObservableList<MedicalHistory> historyList;
    private ObservableList<Appointment> allAppointments;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        historyDAO = new MedicalHistoryDAO();
        historyList = FXCollections.observableArrayList();
        
        setupTable();
        loadPatients();
        setupConverters();
        
        patientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterAppointments(newVal.getId());
                loadHistory(newVal.getId());
            }
        });
        
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void setupTable() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        diagnosesCol.setCellValueFactory(new PropertyValueFactory<>("diagnoses"));
        prescriptionsCol.setCellValueFactory(new PropertyValueFactory<>("prescriptions"));
        historyTable.setItems(historyList);
    }

    private void setupConverters() {
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient p) {
                return p == null ? null : p.getName() + " (ID: " + p.getId() + ")";
            }
            @Override
            public Patient fromString(String s) { return null; }
        });

        appointmentComboBox.setConverter(new StringConverter<Appointment>() {
            @Override
            public String toString(Appointment a) {
                return a == null ? null : a.getAppointmentDate() + " - " + a.getDoctorName();
            }
            @Override
            public Appointment fromString(String s) { return null; }
        });
    }

    private void loadPatients() {
        try {
            patientComboBox.getItems().setAll(patientDAO.getAllPatients());
            allAppointments = FXCollections.observableArrayList(appointmentDAO.getAllAppointments());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load data.");
        }
    }
    
    private void filterAppointments(int patientId) {
        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        for (Appointment a : allAppointments) {
            if (a.getPatientId() == patientId) {
                filtered.add(a);
            }
        }
        appointmentComboBox.setItems(filtered);
    }
    
    private void loadHistory(int patientId) {
        try {
            List<MedicalHistory> histories = historyDAO.getMedicalHistoryByPatient(patientId);
            historyList.setAll(histories);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load medical history.");
        }
    }

    @FXML
    private void handleSave() {
        Patient patient = patientComboBox.getValue();
        if (patient == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a patient.");
            return;
        }

        Appointment appt = appointmentComboBox.getValue();
        int apptId = appt != null ? appt.getId() : 0; // Or handle NULL better if DB allows

        MedicalHistory history = MedicalHistory.builder()
                .patientId(patient.getId())
                .appointmentId(apptId)
                .diagnoses(diagnosesArea.getText())
                .prescriptions(prescriptionsArea.getText())
                .allergies(allergiesArea.getText())
                .testResults(testResultsArea.getText())
                .build();

        try {
            historyDAO.addMedicalHistory(history);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medical history added successfully.");
            clearForm();
            loadHistory(patient.getId());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save medical history: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
    }
    
    private void clearForm() {
        appointmentComboBox.setValue(null);
        diagnosesArea.clear();
        prescriptionsArea.clear();
        allergiesArea.clear();
        testResultsArea.clear();
        historyTable.getSelectionModel().clearSelection();
    }
    
    private void populateFields(MedicalHistory history) {
        diagnosesArea.setText(history.getDiagnoses());
        prescriptionsArea.setText(history.getPrescriptions());
        allergiesArea.setText(history.getAllergies());
        testResultsArea.setText(history.getTestResults());
        
        if (history.getAppointmentId() > 0) {
            appointmentComboBox.getItems().stream()
                .filter(a -> a.getId() == history.getAppointmentId())
                .findFirst()
                .ifPresent(a -> appointmentComboBox.setValue(a));
        } else {
            appointmentComboBox.setValue(null);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
