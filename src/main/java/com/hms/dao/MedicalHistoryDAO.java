package com.hms.dao;

import com.hms.model.MedicalHistory;
import com.hms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryDAO {

    public void addMedicalHistory(MedicalHistory history) throws SQLException {
        String query = "INSERT INTO medical_history (patient_id, appointment_id, diagnoses, prescriptions, allergies, test_results) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, history.getPatientId());
            pstmt.setInt(2, history.getAppointmentId());
            pstmt.setString(3, history.getDiagnoses());
            pstmt.setString(4, history.getPrescriptions());
            pstmt.setString(5, history.getAllergies());
            pstmt.setString(6, history.getTestResults());
            
            pstmt.executeUpdate();
        }
    }

    public List<MedicalHistory> getMedicalHistoryByPatient(int patientId) throws SQLException {
        List<MedicalHistory> historyList = new ArrayList<>();
        String query = "SELECT * FROM medical_history WHERE patient_id = ? ORDER BY recorded_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
             pstmt.setInt(1, patientId);
             try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                     historyList.add(extractHistoryFromResultSet(rs));
                 }
             }
        }
        return historyList;
    }

    private MedicalHistory extractHistoryFromResultSet(ResultSet rs) throws SQLException {
        return MedicalHistory.builder()
                .id(rs.getInt("id"))
                .patientId(rs.getInt("patient_id"))
                .appointmentId(rs.getInt("appointment_id"))
                .diagnoses(rs.getString("diagnoses"))
                .prescriptions(rs.getString("prescriptions"))
                .allergies(rs.getString("allergies"))
                .testResults(rs.getString("test_results"))
                .recordedAt(rs.getTimestamp("recorded_at") != null ? rs.getTimestamp("recorded_at").toLocalDateTime() : null)
                .build();
    }
}
