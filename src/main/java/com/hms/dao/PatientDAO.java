package com.hms.dao;

import com.hms.model.Patient;
import com.hms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public void addPatient(Patient patient) throws SQLException {
        String query = "INSERT INTO patients (name, age, gender, contact, address, blood_group, emergency_contact) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getContact());
            pstmt.setString(5, patient.getAddress());
            pstmt.setString(6, patient.getBloodGroup());
            pstmt.setString(7, patient.getEmergencyContact());
            
            pstmt.executeUpdate();
        }
    }

    public void updatePatient(Patient patient) throws SQLException {
        String query = "UPDATE patients SET name = ?, age = ?, gender = ?, contact = ?, address = ?, blood_group = ?, emergency_contact = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getContact());
            pstmt.setString(5, patient.getAddress());
            pstmt.setString(6, patient.getBloodGroup());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setInt(8, patient.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void deletePatient(int id) throws SQLException {
        String query = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patients ORDER BY id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        }
        return patients;
    }

    public List<Patient> searchPatients(String keyword) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM patients WHERE name LIKE ? OR id = ? OR contact LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            
            int idSearch = -1;
            try {
                idSearch = Integer.parseInt(keyword);
            } catch (NumberFormatException e) {
                // Ignore, it's not an ID
            }
            pstmt.setInt(2, idSearch);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(extractPatientFromResultSet(rs));
                }
            }
        }
        return patients;
    }

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        return Patient.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .age(rs.getInt("age"))
                .gender(rs.getString("gender"))
                .contact(rs.getString("contact"))
                .address(rs.getString("address"))
                .bloodGroup(rs.getString("blood_group"))
                .emergencyContact(rs.getString("emergency_contact"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build();
    }
}
