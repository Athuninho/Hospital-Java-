package com.hms.controller;

import com.hms.dao.AppointmentDAO;
import com.hms.dao.DoctorDAO;
import com.hms.dao.PatientDAO;
import com.hms.model.Appointment;
import com.hms.model.Doctor;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {

    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Doctor> doctorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> statusComboBox;
    
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> idCol;
    @FXML private TableColumn<Appointment, String> patientCol;
    @FXML private TableColumn<Appointment, String> doctorCol;
    @FXML private TableColumn<Appointment, LocalDate> dateCol;
    @FXML private TableColumn<Appointment, LocalTime> timeCol;
    @FXML private TableColumn<Appointment, String> statusCol;
    
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private ObservableList<Appointment> appointmentList;
    private Appointment selectedAppointment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
        doctorDAO = new DoctorDAO();
        appointmentList = FXCollections.observableArrayList();
        
        statusComboBox.getItems().addAll("Pending", "Confirmed", "Completed", "Cancelled");
        
        setupTable();
        loadData();
        setupComboBoxConverters();
        
        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAppointment = newSelection;
                populateFields(selectedAppointment);
            }
        });
    }

    private void setupTable() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        appointmentTable.setItems(appointmentList);
    }

    private void setupComboBoxConverters() {
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient p) {
                return p == null ? null : p.getName() + " (ID: " + p.getId() + ")";
            }
            @Override
            public Patient fromString(String s) {
                return null;
            }
        });

        doctorComboBox.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor d) {
                return d == null ? null : d.getName() + " (" + d.getSpecialization() + ")";
            }
            @Override
            public Doctor fromString(String s) {
                return null;
            }
        });
    }

    private void loadData() {
        try {
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            appointmentList.setAll(appointments);
            
            List<Patient> patients = patientDAO.getAllPatients();
            patientComboBox.getItems().setAll(patients);
            
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            doctorComboBox.getItems().setAll(doctors);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        Patient patient = patientComboBox.getValue();
        Doctor doctor = doctorComboBox.getValue();
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText());
        String status = statusComboBox.getValue();

        try {
            // Check double booking
            if (selectedAppointment == null || 
                !(selectedAppointment.getDoctorId() == doctor.getId() && 
                  selectedAppointment.getAppointmentDate().equals(date) && 
                  selectedAppointment.getAppointmentTime().equals(time))) {
                
                List<Appointment> doctorAppts = appointmentDAO.getAppointmentsByDoctor(doctor.getId());
                for (Appointment appt : doctorAppts) {
                    if (appt.getAppointmentDate().equals(date) && appt.getAppointmentTime().equals(time) && !appt.getStatus().equals("Cancelled")) {
                        showAlert(Alert.AlertType.WARNING, "Double Booking", "This doctor already has an appointment at this time.");
                        return;
                    }
                }
            }

            if (selectedAppointment == null) {
                Appointment appt = Appointment.builder()
                        .patientId(patient.getId())
                        .doctorId(doctor.getId())
                        .appointmentDate(date)
                        .appointmentTime(time)
                        .status(status)
                        .build();
                appointmentDAO.addAppointment(appt);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment booked successfully.");
            } else {
                // For simplicity, we only update status in this basic implementation, 
                // but usually you might update time/date too.
                appointmentDAO.updateAppointmentStatus(selectedAppointment.getId(), status);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment status updated.");
            }
            clearFields();
            loadData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try {
                appointmentDAO.deleteAppointment(selectedAppointment.getId());
                clearFields();
                loadData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete appointment: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearFields() {
        selectedAppointment = null;
        patientComboBox.setValue(null);
        doctorComboBox.setValue(null);
        datePicker.setValue(null);
        timeField.clear();
        statusComboBox.setValue("Pending");
        appointmentTable.getSelectionModel().clearSelection();
    }

    private void populateFields(Appointment appointment) {
        patientComboBox.getItems().stream().filter(p -> p.getId() == appointment.getPatientId()).findFirst().ifPresent(p -> patientComboBox.setValue(p));
        doctorComboBox.getItems().stream().filter(d -> d.getId() == appointment.getDoctorId()).findFirst().ifPresent(d -> doctorComboBox.setValue(d));
        datePicker.setValue(appointment.getAppointmentDate());
        timeField.setText(appointment.getAppointmentTime().toString());
        statusComboBox.setValue(appointment.getStatus());
    }

    private boolean validateInput() {
        if (patientComboBox.getValue() == null || doctorComboBox.getValue() == null || 
            datePicker.getValue() == null || timeField.getText().isEmpty() || statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return false;
        }
        try {
            LocalTime.parse(timeField.getText());
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Time must be in HH:mm format.");
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
