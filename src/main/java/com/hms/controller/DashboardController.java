package com.hms.controller;

import com.hms.util.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalPatientsLabel;

    @FXML
    private Label totalDoctorsLabel;

    @FXML
    private Label todaysAppointmentsLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // Total Patients
            ResultSet rsPatients = stmt.executeQuery("SELECT COUNT(*) FROM patients");
            if (rsPatients.next()) {
                totalPatientsLabel.setText(String.valueOf(rsPatients.getInt(1)));
            }

            // Total Doctors
            ResultSet rsDoctors = stmt.executeQuery("SELECT COUNT(*) FROM doctors");
            if (rsDoctors.next()) {
                totalDoctorsLabel.setText(String.valueOf(rsDoctors.getInt(1)));
            }

            // Today's Appointments
            ResultSet rsAppointments = stmt.executeQuery("SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()");
            if (rsAppointments.next()) {
                todaysAppointmentsLabel.setText(String.valueOf(rsAppointments.getInt(1)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
