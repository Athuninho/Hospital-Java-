package com.hms.dao;

import com.hms.model.Doctor;
import com.hms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public void addDoctor(Doctor doctor) throws SQLException {
        String query = "INSERT INTO doctors (name, specialization, contact, consultation_fee, available_days, available_hours) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getContact());
            pstmt.setDouble(4, doctor.getConsultationFee());
            pstmt.setString(5, doctor.getAvailableDays());
            pstmt.setString(6, doctor.getAvailableHours());
            
            pstmt.executeUpdate();
        }
    }

    public void updateDoctor(Doctor doctor) throws SQLException {
        String query = "UPDATE doctors SET name = ?, specialization = ?, contact = ?, consultation_fee = ?, available_days = ?, available_hours = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getContact());
            pstmt.setDouble(4, doctor.getConsultationFee());
            pstmt.setString(5, doctor.getAvailableDays());
            pstmt.setString(6, doctor.getAvailableHours());
            pstmt.setInt(7, doctor.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void deleteDoctor(int id) throws SQLException {
        String query = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT * FROM doctors ORDER BY name ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                doctors.add(extractDoctorFromResultSet(rs));
            }
        }
        return doctors;
    }
    
    public Doctor getDoctorById(int id) throws SQLException {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             pstmt.setInt(1, id);
             try (ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return extractDoctorFromResultSet(rs);
                 }
             }
        }
        return null;
    }

    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        return Doctor.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .specialization(rs.getString("specialization"))
                .contact(rs.getString("contact"))
                .consultationFee(rs.getDouble("consultation_fee"))
                .availableDays(rs.getString("available_days"))
                .availableHours(rs.getString("available_hours"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build();
    }
}
